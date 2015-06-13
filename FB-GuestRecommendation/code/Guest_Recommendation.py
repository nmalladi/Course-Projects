# -*- coding: cp1252 -*-
import facebook
import json
import pdb
import PlaceInfo
import PhotoInfo
import PostInfo
import numpy as np
from scipy.cluster.vq import *
from scipy import stats


ACCESS_TOKEN='CAAD4HSRDa68BAD1jEPHTk6SFbpyklxuiPzVovdZADkB8LI78AShn3huzi7HzkWAuHwVtu4Yy2pukh8XUe9JBJvZAd6ZBl2Xpfnp3IbnK8nVAsNduZAdP2LyrOJjWyyZBZC3uoIjo9ToIFtEd6SDQQtcMKqfOaSZA6PCx6qZBOPJvys5Ag5v5xlZCb9iuoCrdPmmMu3T6WFCKRGZAUgCxE1ms1K'

def pp(o):
    print json.dumps(o, indent=1)

def make_facebook_connections_call(gApi, id, edgeName):

    
    response = gApi.get_connections(id, edgeName)['data']

    return response

# rankOrderCentroid implementation to calculate weightage
def rankOrderCentroid(noItems, rank):

    result = 0.00
    sum = 0.00
    
    for n in range(rank, noItems+1):
        sum += 1.00/n
        
    result = sum/noItems

    return round(result,2)

# this method is responsible for invoking facebook profile mining
def mine_information(gApi, id):

    #decleration
    friendsList = []
    friendsScore = {}
    indivisualScore = 0

    currUserProfile = gApi.get_object(id)
    friendsList = make_facebook_connections_call(gApi, id, 'friends')

    # create a dictionary of friends
    friendsScore = {frnd['id']:{} for frnd in friendsList}
    
    PlaceInfo.find_location_score(gApi, currUserProfile, friendsScore)
    print 'complete location score'
    
    PhotoInfo.harvest_photo_info(gApi, friendsScore)
    print 'complete harvest photo'

    PostInfo.harvest_posts(gApi,friendsScore)
    print 'complete harvest post'
    
    #pp(friendsScore)

    return friendsScore

# this method calculates the weighted score for each friend of the user
def process_weighted_score(friendsScore):

    # calculate weights
    w_tags = rankOrderCentroid(4,1)
    w_comments = rankOrderCentroid(4,2)
    w_place = rankOrderCentroid(4,3)
    w_likes = rankOrderCentroid(4,4)

    result = {}

    # iterate through friends record and calculate the weighted score
    for rec in friendsScore:
        sum = 0.00
        # iterate different observations - palce, comments, tags, likes
        for id in friendsScore[rec]:

            if id == 'place':
                sum+= friendsScore[rec][id] * w_place
            elif id == 'comments':
                sum+= friendsScore[rec][id] * w_comments
            elif id == 'tags':
                sum+= friendsScore[rec][id] * w_tags
            elif id =='likes':
                sum+= friendsScore[rec][id] * w_likes

        result[rec] = round(sum,2)

    return result

# utility method to display the results
def display_results(option,cluster,res):

    if option == 2:

        print "Guest Recommendations:"
        print
        print "Most Likely:"
        pp(cluster[0])
        print
        print "Likely:"
        pp(cluster[1])

    if option == 1:
        
        max = res[0]
        index1 = 0
        # determine the index of most likely cluster from centroids
        for id in range(1,3):
            if(max < res[id]):
                max = res[id]
                index1 = id

        print "Guest Recommendations:"
        print
        print "Most Likely:"
        pp(cluster[index1])

        
        index2 = 0
        max1 = 0
        # determine the index of likely cluster from centroids
        if len(res) > 2:
            for id in range(1,3):
                if(max1 < res[id] and res[id] != max ):
                    max1 = res[id]
                    index2 = id

            print
            print "Likely:"
            pp(cluster[index2])
        

# this method implements the clustering logic        
def cluster_data(weightedScore, option, centroids=None):

    clusters= {}
    itr = 0
    scores = np.array(weightedScore.values())

    #Normalization
    zscore = stats.zscore(scores)
    
    # random initialization
    if option==1:
        k = 3 # number of c
        
    elif option ==2 and len(centroids) == 3: # pre-defined centroids

        keys = weightedScore.keys()
        index1 = keys.index(centroids[0]) #Most Likely
        index2 = keys.index(centroids[1]) #Likely
        index3 = keys.index(centroids[2]) #Unlikely

        k  = np.array((zscore[index1],zscore[index2],zscore[index3]))
    else:
        print "Incorrect Parameters"
        return cluster

    #res - centroids
    #idx -  idx[i] index of the centroid the i’th observation is closest to.
    res, idx = kmeans2(zscore,k)    

    for i in range(0,3):
        clusters[i] = []
      
    for rec in weightedScore.keys():

        clusters[idx[itr]].append(rec)
        itr = itr+1

    display_results(option,clusters,res)
    
    return clusters

def setup_data_clustering():

    weightedScore = {}

    try:
        gApi = facebook.GraphAPI(ACCESS_TOKEN)

        friendsScore = mine_information(gApi, 'me')

        weightedScore = process_weighted_score(friendsScore)

    except Exception as e:
        print repr(e)
        
    return weightedScore

__name__ = "main"

try:
    # cluster_data(weightedScore, option)
    # option = 1: random initialization of centroid
    # option = 2: pre-selected centroid, 3 centroids needs to be provided
    # centroid[0]: provide facebook id who is "most likely" to be invited
    # centroid[1]: provide facebook id who is "likely" to be invited
    # centroid[2]: provide facebook id with least interaction


    weightedScore = setup_data_clustering()

    
    # Not recommended: as your are requried to run it multiple 
    # times to retrieve consistent result use option 2
    

    print "Random Cluster Initialization"
    if len(weightedScore) > 0:
        cluster = cluster_data(weightedScore, 1)

    
    #uncomment below to use option 2
    '''
    # replace ids with ids of your friends 
    print
    print "pre determined centroids"
    centroids = []
    centroids.append('1233261794') #most likely
    centroids.append('1078052768') #likely
    centroids.append('1354244022') #unlikely
    cluster = cluster_data(weightedScore, 2, centroids)
    '''
    

except Exception as e:
    print repr(e)
    

    

    
    



    

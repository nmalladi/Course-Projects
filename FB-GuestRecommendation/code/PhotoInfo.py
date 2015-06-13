import facebook
import json
from datetime import date
from datetime import timedelta
import pdb

def pp(o):
    print json.dumps(o, indent=1)

#this method retrieves the ids of facebook uses who have commented on the photos
def harvest_comments(gApi, photo):

    ids = []

    if 'comments' in photo:
        ids = [comment['from']['id'] for comment in photo['comments']['data']]

        if 'next' in photo['comments']['paging']:
            nextPage = photo['comments']['paging']['next']
            next = photo['comments']['paging']['cursors']['after']
            # handling pagination
            while True:

                tokens = nextPage.split('/')
                id = tokens[4]
                attr = tokens[5].split('?')[0]
                

                response = gApi.get_connections(id,attr,limit = 25, after=next)
                ids+= [comment['id'] for comment in response['data']]

                if 'next' in response['paging']:
                    nextPage = response['paging']['next']
                    next = response['paging']['cursors']['after']
                else:
                    break
                
    return ids

# this method is used to retrieve the ids of facebook users
# who are tagged and liked in the photos
def harvest_info(gApi, photo, field):
    
    ids = []

    if field in photo:
        # retrieve 'tags' or 'likes'
        ids = [ rec['id'] for rec in photo[field]['data']]

        
        # handling pagination
        if 'next' in photo[field]['paging']:

            nextPage = photo[field]['paging']['next']
            next = photo[field]['paging']['cursors']['after']
            
            while True:
                
                tokens = nextPage.split('/')
                id = tokens[4]
                attr = tokens[5].split('?')[0]
                
                response = gApi.get_connections(id,attr,limit = 25, after = next)
                ids+= [rec['id'] for rec in response['data']]


                if 'next' in response['paging']:
                    nextPage = response['paging']['next']
                    next = response['paging']['cursors']['after']
                else:
                    break
                
    return ids

# method counts the tags, comments and likes made by a facebook user on
# photos and the count will be stored in the dictionary of friends
def calculate_photo_score(friendsScore, tags, comments, likes):

    keys = friendsScore.keys()
    count = 0

    for type in [tags, comments, likes]:

        keyName = ''
        if count == 0:
            keyName = 'tags'
            count = count + 1
        elif count == 1:
            keyName = 'comments'
            count = count + 1
        elif count == 2:
            keyName = 'likes'
            count = count + 1

        
        for id in type:
            if id in friendsScore:
                if keyName in friendsScore[id]:
                    friendsScore[id][keyName]+= 1
                else:
                    friendsScore[id][keyName] = 1

    return friendsScore
                    
# method retrives the photos of current user and returns the count of
# tagged, comments and likes performed by friends
def harvest_photo_info(gApi, friendsScore, timeLine = 1):

    tags_ids = []
    comments_ids = []
    likes_ids = []
    complete_status = False

    #calculate date

    days_val = 365 * timeLine
    currDate = date.today()
    d = timedelta(days = days_val)
    harvestDate = currDate - d
    
    # retrieve photos
    photos = gApi.get_connections('me', 'photos')

    # handling pagination
    while 'next' in photos['paging']:

        for photo in photos['data']:
 
            if photo['created_time'] > harvestDate.isoformat():

                tags_ids+= harvest_info(gApi, photo, 'tags')
               
                comments_ids+= harvest_comments(gApi, photo)
                
                likes_ids+= harvest_info(gApi, photo, 'likes')

            else:
                complete_status = True
                break

        # handling pagination
        if not complete_status:

            nextPage = photos['paging']['next']
            tokens = nextPage.split('/')
            id = tokens[4]
            attr = tokens[5].split('?')[0]
            next = photos['paging']['cursors']['after']

            #pdb.set_trace()

            photos = gApi.get_connections(id,attr,limit = 25, after = next)
        else:
            break


    calculate_photo_score(friendsScore, tags_ids, comments_ids, likes_ids)

    return friendsScore

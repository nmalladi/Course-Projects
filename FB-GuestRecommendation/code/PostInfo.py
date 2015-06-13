import facebook
import json
from datetime import date
from datetime import timedelta
import pdb

def pp(o):
    print json.dumps(o, indent=1)

#this method retrieves the ids of facebook uses who have commented on posts
def harvest_comments(gApi, photo):

    ids = []

    if 'comments' in photo:
        ids = [comment['from']['id'] for comment in photo['comments']['data']]

        if 'next' in photo['comments']['paging']:
            nextPage = photo['comments']['paging']['next']
            next = photo['comments']['paging']['cursors']['after']

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
# who are tagged and liked in the posts
def harvest_info(gApi, photo, field):
    
    ids = []

    if field in photo:

        ids = [ rec['id'] for rec in photo[field]['data']]

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

# method counts the omments and likes made by a facebook user on
# posts and the count will be stored in the dictionary of friends
def calculate_post_score(friendsScore, comments, likes):

    keys = friendsScore.keys()
    count = 0

    for type in [comments, likes]:

        keyName = ''
        if count == 0:
            keyName = 'comments'
            count = count + 1
        elif count == 1:
            keyName = 'likes'
        
        for id in type:
            if id in friendsScore:
                if keyName in friendsScore[id]:
                    friendsScore[id][keyName]+= 1
                else:
                    friendsScore[id][keyName] = 1

    return friendsScore

# method retrives the posts of current user and returns the count of
# comments and likes performed by friends
def harvest_posts(gApi, friendsScore, timeLine = 1):

    comments_ids = []
    likes_ids = []
    complete_status = False

    #calculate date

    days_val = 365 * timeLine
    currDate = date.today()
    d = timedelta(days = days_val)
    harvestDate = currDate - d

    posts = gApi.get_connections('me','posts')

    while 'next' in posts['paging']:

        for post in posts['data']:

            if post['created_time'] > harvestDate.isoformat():

                if 'status_type' in post and \
                   (post['status_type'] == 'mobile_status_update' or \
                    post['status_type'] == 'wall_post' or \
                    post['status_type'] =='status_update'):
                        
                    comments_ids+= harvest_comments(gApi, post)
                    likes_ids+= harvest_info(gApi, post, 'likes')
            else:
                complete_status = True
                break

        #pdb.set_trace()          

        if not complete_status:
            nextPage = posts['paging']['next']
            tokens = nextPage.split('/')
            id = tokens[4]
            attr = tokens[5].split('?')[0]
            temp = tokens[5].split('&')
            next = temp[2].split('=')[1]
                    
            posts = gApi.get_connections(id,attr,limit = 25, until = next)
        else:
            break

    #pdb.set_trace()
    calculate_post_score(friendsScore, comments_ids, likes_ids)
    
    return friendsScore


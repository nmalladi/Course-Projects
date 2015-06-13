import facebook
import json

def pp(o):
    print json.dumps(o, indent=1)

# retrieves location information and calculates a location score based on the match
def find_location_score(gApi, currUser, friendsScore, location= None):

    homeTown = ''
    currLoc = ''
    userProfile = {}
    workLoc = []
    score = 0
    
    currUsrHT = currUser['hometown']['name'] # current user home town
    currUsrCL = currUser['location']['name'] # current user location
    # current user work locations
    currUsrWorkLoc = set([rec['employer']['name'] for rec in currUser['work']])

    # for every friend compare against current user locations
    for id in friendsScore:
        
        score = 0;
        userProfile = gApi.get_object(id)
        
        
        if 'hometown' in userProfile:
            homeTown = userProfile['hometown']['name']

            if homeTown == currUsrHT:
                score+= 1

        if 'location' in userProfile:
            currLoc = userProfile['location']['name']

            if currLoc == currUsrCL:
                score+= 1

        if 'work' in userProfile:
            
            
            workLoc = set([rec['employer']['name'] for rec in userProfile['work']])
            #pdb.set_trace()
            #print workLoc
            tempRes = currUsrWorkLoc & workLoc
            score += len(tempRes)

        friendsScore[id]['place'] = score

    return friendsScore

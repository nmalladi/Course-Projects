INSTALLATION NOTES:

System Requirements:
- python 2.7
- Latest version facebook-sdk
- Latest version of sciPy library
- requires facebook application with Graph API 1.0

INSTALLATION

1. Install the facebook sdk using pip install or easy_install
2. Install the sciPy using pip install
3. Add yourself as developer on a facebook application with Graph API 1.0
4. Generate Access token and select the following permissions
	
	User Data Permissions
	•	user_about_me, user_activities, user_photos, 
		user_work_history, user_status, user_friends, 
		user_hometown, user_location
		
	Friends Data Permissions
	•	friends_about_me, friends_work_history, friends_hometown, friends_location
	
	Extened Data Permissions
	•	read_stream, read_requests, export_stream
	
5. go to the file Guest_Recommendation.py and paste the newly generated access token

6. Option 1 - random initialization, run Guest_Recommendation.py from code directory
   Note: Need to run multiple times for satisfactory result, use option 2 instead

7. For option 2 - pre-selected centroid, select three friend ids which satisfy the following

	At Graph API Explorer query -> /v.1/me/friends

	centroid[0]: provide facebook id who is "most likely" to be invited
    centroid[1]: provide facebook id who is "likely" to be invited
    centroid[2]: provide facebook id with least interaction
	
	Replace them with the selected ones
	
7. run Guest_Recommendation.py from code directory

	
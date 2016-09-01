# PhotoManager

##Using Libraries
- [StickyListHeaders](https://github.com/emilsjolander/StickyListHeaders)
- [Glide](https://github.com/bumptech/glide)

##Activity structure
- [MainActivity](https://github.com/poap/PhotoManager/blob/master/app/src/main/java/com/poap/photomanager/MainActivity.java)
 - For main story list
 - <img src="/screenshot/main.png" width="50%">
- [StoryViewActivity](https://github.com/poap/PhotoManager/blob/master/app/src/main/java/com/poap/photomanager/StoryViewActivity.java)
 - For viewing, editing each story
 - <img src="/screenshot/story_view.png" width="50%">
 - <img src="/screenshot/story_edit.png" width="50%">
- [FullscreenImage](https://github.com/poap/PhotoManager/blob/master/app/src/main/java/com/poap/photomanager/FullscreenImage.java)
 - For viewing full-screen size image

##Database structure
### [StoryDB](https://github.com/poap/PhotoManager/blob/master/app/src/main/java/com/poap/photomanager/db/StoryDB.java)
- table 'story'
 - column 'title': title of story (text type)
 - column 'memo': memo of story (text type)
 - column 'edited': edited datetime of story (timestamp type)
- table 'picture'
 - column 'path': URL of image (text type)
 - column 'story': foreign key references story row id
 
##Future works (see TODO items on android studio)
 - Implement search function
 - Show number of stories in each group
 - Show confirm alert when user apply and cancel to edit story
 - Implement function for delete story
 - Implement removing image

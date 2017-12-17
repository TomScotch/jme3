cd ~/jme3/GravityBasedPhysics/dist/ &&
zipalign -p 4 MyGame-release-unsigned.apk MyGame.apk &&
apksigner sign --ks ~/jme3/my-release-key.keystore --ks-key-alias tomscotch MyGame.apk &&
adb install MyGame.apk -s

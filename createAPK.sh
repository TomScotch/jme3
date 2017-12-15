apksigner sign --ks jme3/my-release-key.keystore --ks-key-alias tomscotch MyGame.apk
zipalign -p 4 MyGame.apk MyGame-aligned.apk
adb install MyGame-aligned.apk -s

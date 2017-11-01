ant -f BasicGame -Dnb.internal.action.name=rebuild clean jar ;
rm ../simple-server-node/data/myGame.zip &&
zip ../simple-server-node/data/myGame.zip BasicGame/dist/ -r

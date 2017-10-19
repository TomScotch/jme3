cd /home/tomscotch/jme3/ ;
git pull ;
ant -f /home/tomscotch/jme3/BasicGame -Dnb.internal.action.name=rebuild clean jar ;
rm /home/tomscotch/simple-server-node/data/myGame.zip ;
zip /home/tomscotch/simple-server-node/data/myGame.zip /home/tomscotch/jme3/BasicGame/dist/ -r ;
sudo resin sync --source /home/tomscotch/simple-server-node/data/ --destination /opt

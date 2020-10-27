from com.codebroker.api import AppListener;
from com.codebroker.api import IGameUser;
from com.codebroker.api import AppContext;
from Demo1 import Demo;


class PythonScriptListener(AppListener):
    def __init__(self):
        print "init hello"
        demo=Demo()

    def init(self,obj):
        print AppContext;
        print obj

    def sessionLoginVerification(self,name,parameter):
        print "hello"
        print name
        print parameter

    def userLogin(self,user):
        print "hello"
        print user.getUserId()

    def handleLogout(self,user):
        print "hello"

    def userReconnection(self,user):
        print "hello"

    def handleClientRequest(self,user, requestId,params):
        print "hello"
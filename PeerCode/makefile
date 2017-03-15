JCC=javac

JFLAGS = -g

default: Peer.class PeerServer.class PeerForLookUpTest.class PeerRegisterTest.class

Peer.class : Peer.java
	$(JCC) $(JFLAGS) Peer.java

PeerServer.class : PeerServer.java
	$(JCC) $(JFLAGS) PeerOperate.java

PeerForLookUpTest.class : PeerForLookUpTest.java
	$(JCC) $(JFLAGS) PeerForLookUpTest.java

PeerRegisterTest.class : PeerRegisterTest.java
	$(JCC) $(JFLAGS) PeerRegisterTest.java
clean:
	$(RM) *.class

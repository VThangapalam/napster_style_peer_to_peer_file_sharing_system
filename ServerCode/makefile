JCC=javac

JFLAGS = -g

default: IndexServer.class IndexRegisterOperate.class

IndexServer.class : IndexServer.java
	$(JCC) $(JFLAGS) IndexServer.java

IndexRegisterOperate.class : IndexRegisterOperate.java
	$(JCC) $(JFLAGS) IndexRegisterOperate.java

clean:
	$(RM) *.class

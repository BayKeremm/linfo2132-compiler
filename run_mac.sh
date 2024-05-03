gradle run --args="-parser refactor.lang" &&
java refactor &&

gradle run --args="-parser file.lang" &&
java file

#gradle run --args="-parser refactor.lang" &&
#java refactor

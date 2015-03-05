Part I:
	GenerateModel.java
	GenerateTags.java
Part II:
	Method A:
		GenerateModel.java
		GenerateTagsTwoA.java
	Method B:
		GenerateModelTwoB.java
		GenerateTagsTwoB.java


Instructions to run the program:
1. put all above java files with treebank-sentences.txt file in same directory
2. open terminal and compile and run java file as below:
	> javac <filename>.java
	> java filename	



File explaination:
First java file above in each category is used to generate the model and second one is used to generate the tags.
Model generation is a small file just training the model and storing model in bin file.
Tags generation files use to generate tags on testing data using generated model and generate confusion matrix, per tag accuracy.
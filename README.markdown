Ant Resource Compiler
=====================

Aim
---

Since I have started programming for Android I ever wished to have a resource management system for Java like the Android one. This is my first attempt to create a similar approach using an ANT precompiler for Java.

How it works
------------

For now the resources are limited to Strings and PNG Images. You can access them after resource compilation as follows:

	JLabel myLabel = new JLabel()
	myLabel.setText(R.string.MY_LABEL_TEXT.string());
	Icon myIcon = new ImageIcon(R.drawable.MY_TEXT_ICON.url());
	myLabel.setIcon(myIcon);

Usage
-----

#### 1.
Structure your Java Project like this

	[Root]
	./src (holds Java source files)
	./res/drawable
	./res/string
	./res/resources

#### 2.
Put the folder *res* into the classpath.

#### 3.
Add *PNG* images to the drawable folder. __Important__: Those files need to have all lowercase names that could also represent a Java variable name, e.g. `my_nice_icon.png`.

#### 4.
Add the strings into a `string.properties` file. Again you will need lowercase variables that are valid Java identifiers. Example
`my_string=Foo Bar`
In a feature version the resource compiler will give an error upon compilation if non-valid names are detected.

#### 5.
Add the `ant-resource-compiler.jar` to the `lib` folder of your project (needs to be in the classpath as well!)

#### 6.
Add the following to your `build.xml`

	<path id="build.antlib">
		<fileset dir="./lib" includes="*.jar" />
	</path>
	
	<target name="rc" description="Compile resources">
		<taskdef name="resourceCompile" classname="com.mpdeimos.ant.resourcecompiler.ResourceCompilerTask" classpathref="build.antlib"/>
		<resourceCompile path="./res"/>
	</target>

#### 7.
Run `ant rc` to compile your resources.

Get the JAR
-----------

For now I do not provide a JAR download, so you need to create this one on your own by cloning this repository to your HD (or simply download the tarball) and invoke `ant jar`.
After compilation the JAR is located at `./build/jars/ant-resource-compiler.jar`

Final Notes
-----------

* I provide the script 'as-is'. So do not expect that it will work for your project.
* The current implementation is still raw, but does what it should do for my project. 
* If you want to add some stuff, feel free to fork.
* An example of how this ant script is used can be found at http://code.launchpad.net/~mpdeimos/+junk/tensor
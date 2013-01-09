# [Astah Easy Code Reverse Plug-in](http://astah.net/features/code-reverse-plugin)
This enables you to reverse Java source code on Github, Bitbucket, Google Project Hosting and other repository services into UML Class Diagram just by drag and drop the URL to [Astah](http://astah.net), also you can drag and drop .java files directly to Astah to do the same to instantly make the source code visible.

**Demo Movie**

<http://www.youtube.com/watch?v=_7shceFgfVw>


## Available for
Astah Professional, Astah UML (6.5 or later)


## Ability
* Drag and drop URL/Link of source code on Repository to Astah to create UML Class Diagram
* Drag and drop .java files in local workplace to Astah to create UML Class Diagram


## This Plug-in does not currently support to
* analyze annotations
* create Associations in Class Diagram


## How to install
1. Deploy the jar file you downloaded from [Astah Easy Code Reverse](http://cdn.change-vision.com/plugins/easycodereverse-1.0.1.jar), in the **"plugins"** folder…

   e.g.) for Professional edition
   
   `$USER_HOME/.astah/professional/plugins/`
   
   `/Applications/astah professional/plguins/`
   
   `C:¥Program Files¥astah-professional¥plugins/`
   
   e.g.) for UML edition
   
   `$USER_HOME/.astah/uml/plugins/`
   
   `/Applications/astah UML/plguins/`
   
   `C:¥Program Files¥astah-UML¥plugins¥`

2. Deploy the **[easycodereverse-dict.json](https://github.com/ChangeVision/astah-easycodereverse-plugin/blob/master/easycodereverse-dict.json)** file in `$USER_HOME/.astah/professional/` or `$USER_HOME/.astah/uml/`


## How to work
Drag & drop .java files from local workplace or URL/Link of Java source code on repository directly to Astah. Astah generates UML Class diagrams with them.

**Demo Movie**

<http://www.youtube.com/watch?v=_7shceFgfVw>


## How to build
1. Install the Astah Plug-in SDK - <http://astah.net/features/sdk>
1. `git clone git://github.com/ChangeVision/astah-easycodereverse-plugin.git`
1. `cd easycodereverse`
1. `astah-build`
1. `astah-launch`

#### Generating config to load classpath [for Eclipse](http://astah.net/tutorials/plug-ins/plugin_tutorial_en/html/helloworld.html#eclipse)

 * `astah-mvn eclipse:eclipse`


## URL used in Demo Movie
* Github:

  <https://github.com/KentBeck/junit/blob/master/src/main/java/junit/extensions/ActiveTestSuite.java>

* Google Project Hosting:

  <http://code.google.com/p/google-web-toolkit/source/browse/trunk/dev/core/src/com/google/gwt/dev/GWTShell.java>

* Bitbucket:

  <https://bitbucket.org/jmurty/jets3t/src/844ad30e3c13/src/org/jets3t/service/S3ServiceException.java>


## About easycodereverse-dict.json
**[easycodereverse-dict.json](https://github.com/ChangeVision/astah-easycodereverse-plugin/blob/master/easycodereverse-dict.json)** is used to convert the URL to extract only the source code from the repository to Astah. By customizing this file, you will be able to reverse source code on Redmine or SourceForge.

e.g.)

**With revisions**

<https://fisheye2.atlassian.com/browse/mockito/trunk/src/org/mockito/Answers.java?r=1928>

**raw file**

<https://fisheye2.atlassian.com/browse/~raw,r=1928/mockito/trunk/src/org/mockito/Answers.java>


## Note
While you have the Easy Code Reverse Plug-in on, every time you drag and drop any files into Class Diagram, a dialog appears to ask if you want to analyze the source code even though the file you are dropping is not .java.


## License
Copyright 2012 Change Vision, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
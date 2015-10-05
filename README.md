# UEDashboard

UEDashboard is a tool which automatically detects unusual events in a commit history based on metrics and smells, and surfaces them in an event feed.

The project is divided into: (i) a Java project which collects and analyzes data from commit history, and (ii) a web project (inside the folder Web Dashboard) developed in Python using the framework Flask, where unusual events are displayed in an event feed.

UEDashboard is going through some improvements, but feel free to contribute or give any suggestions.

Blog post about UEDashboard: http://larissaleite.github.io/blog.html

### Instructions to download and run the project:

#### Java Project

1. Import the project as a Maven project on Eclipse.
2. Create your own database (the code is configured for MySQL, but feel free to change that)
3. Add your database settings in `UEDashboard/src/main/java/br/ufrn/uedashboard/dao/MySQLConnector.java`
4. Add the path to where you want to store your spreadsheets in both `UEDashboard/src/main/java/br/ufrn/uedashboard/csv/CSVWriter.java` and `UEDashboard/src/main/java/br/ufrn/uedashboard/csv/CSVReader.java`
5. Add the information of your SVN Repository in `UEDashboard/src/main/java/br/ufrn/uedashboard/main/Main.java`
6. Run the project!

#### Web App 

1. Add your SVN username and database settings in `WebDashboard/app/routes.py`
2. Run the project `python app/routes.py`

Should you have any questions, don't hesitate to contact us!

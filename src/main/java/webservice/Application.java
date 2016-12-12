package main.java.webservice;


public class Application {
	public static void main(String[] args) {
		new populator.PageGetter("http://www.cochranelibrary.com/home/topic-and-review-group-list.html?page=topic");
        //SpringApplication.run(Application.class, args);
    }
}

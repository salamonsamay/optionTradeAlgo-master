package mycode.data;

public interface Request <T>{

     T endpoint();
     T build();
     T getRequest(String url);
     T requestWithNextUrl(String url);

}

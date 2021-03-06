package com.serendipity.rest;

import com.serendipity.dao.DAOBook;
import com.serendipity.dao.DAOCategory;
import com.serendipity.model.Category;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


/**
 *
 * @author Vladimir
 */
@Path("/v1/category")
public class RESTCategory {
    
    /**
     * The method creates its own HTTP response with the list of categories
     * Ex: http://localhost:8080/book_shop/api/v1/category
     * 
     * @return - the response with the category list
     * @throws Exception 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryList() throws Exception {
        
        String returnString = null;
        JSONArray jsonArrayCategoryList = new JSONArray();
        
        DAOCategory daoCategory = new DAOCategory();
        JSONObject jsonObject = new JSONObject();
        
        try {
            
            jsonArrayCategoryList = daoCategory.getAllCategoriesJSONREST();
        
            // get books belonging to category
            for(int i = 0; i < jsonArrayCategoryList.length(); i++) {
                
                JSONObject obj = jsonArrayCategoryList.getJSONObject(i);
                DAOBook daoBook = new DAOBook();
                
//                Create a json array of the following format:
//                {"category_id":{"category_id":"1","category_name":"Category 1","book_list":["Title 1","Title 2"]}, ...}
                obj.put("category_id", obj.getString("category_id"));
                obj.put("category_name", obj.getString("category_name"));
                obj.put("book_list", daoBook.getBookListInfoByCategoryId( obj.getInt("category_id") ));
                returnString = jsonObject.put(Integer.toString(obj.getInt("category_id")), obj).toString();
            }
        
        } catch (Exception e) {
            jsonObject.put("HTTP_CODE", "500");
            jsonObject.put("MSG", "Server unable to process get category request!");
            return Response.ok(jsonArrayCategoryList.put(jsonObject).toString()).build();
        }
                
        return Response.ok(returnString).build();
    }
    
    /**
     * The method creates its own HTTP response and searches Book table by author
     * 
     * @param categoryInfo
     * @return - the response with the book name
     * @throws Exception 
     */
    @GET
    @Path("/search/category/{categoryName}")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON}) // access both form and json data
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCategoryByCategoryName(@PathParam("categoryName") String categoryInfo) throws Exception {
        
        String returnString;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject obj;
        DAOCategory daoCategory = new DAOCategory();
        JSONArray jsonArrayCategoryList  = new JSONArray();
            
        String categoryName = categoryInfo;
            
//      validate category name
        if (categoryName == null || categoryName.length() == 0 || !categoryName.matches(".*\\w.*")) {
            jsonObject.put("HTTP_CODE", "500");
            jsonObject.put("MSG", "Enter a valid category!");
            return Response.ok(jsonArray.put(jsonObject).toString()).build();
        }
        
        try {
            jsonArrayCategoryList = daoCategory.searchCategoryByCategoryName(categoryName);

            // get books belonging to category
            for(int i = 0; i < jsonArrayCategoryList.length(); i++) {

                obj = jsonArrayCategoryList.getJSONObject(i);

                obj.put("category_id", obj.getInt("category_id"));
                obj.put("category_name", obj.getString("category_name"));

                jsonObject.put(Integer.toString(obj.getInt("category_id")), obj);
            }
            returnString = jsonObject.put("HTTP_CODE", "200").toString();
        } catch (JSONException e) {
            jsonObject.put("HTTP_CODE", "500");
            jsonObject.put("MSG", "Server unable to process get book request!");
            return Response.ok(jsonArrayCategoryList.put(jsonObject).toString()).build();
        }
        
        return Response.ok(returnString).build();
    }
    
    /**
     * The method creates its own HTTP response and adds category to the 
     * database based on category name passed by the client
     * 
     * @param categoryInfo
     * @return - the response with the category name
     * @throws Exception 
     */
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON}) // access both form and json data
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(String categoryInfo) throws Exception {
        
        String returnString;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        DAOCategory daoCategory = new DAOCategory();
         
        try {
            
            JSONObject partsData = new JSONObject(categoryInfo);
            
            String categoryTitle = partsData.optString("category_title");
            
            if (categoryTitle == null || categoryTitle.length() == 0 || !categoryTitle.matches(".*\\w.*")) {
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Enter a valid category name!");
                return Response.ok(jsonArray.put(jsonObject).toString()).build();
            }
            
            int http_code = daoCategory.addCategory(categoryTitle);
            
            if(http_code == 200) {
                
                jsonObject.put("HTTP_CODE", "200");
                jsonObject.put("MSG", "Category has been entered successfully!");
                returnString = jsonArray.put(jsonObject).toString();
                
            } else {
                //return Response.status(500).entity("Unable to process add category").build();
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Enter a valid category!");
                returnString = jsonArray.put(jsonObject).toString();
            }
            
        } catch (Exception e) {
            //return Response.status(500).entity("Server unable to process request.").build();
            jsonObject.put("HTTP_CODE", "500");
            jsonObject.put("MSG", "Server unable to process request!");
            returnString = jsonArray.put(jsonObject).toString();
        }
        
        return Response.ok(returnString).build();
    }
    
    /**
     * This method will allow you to update data in the category table.
     * In this example we are using both PathParams and the message body (payload).
     * @param categoryInfo
     * @return 
     * @throws java.lang.Exception
     */
    @PUT
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(String categoryInfo) throws Exception {
        
        String categoryId;
        String categoryName;
        Category category;
        DAOCategory daoCategory = new DAOCategory();
        String returnString;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        
        try {
            
            JSONObject partsData = new JSONObject(categoryInfo);
            categoryId = partsData.optString("category_id");
            categoryName = partsData.optString("category_name");
            
//            validate category id
            int catId;
            try {
                catId = Integer.parseInt(categoryId);
            } catch (NumberFormatException e) {
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Category id is not valid!");
                return Response.ok(jsonArray.put(jsonObject).toString()).build();
            }
            
//            validate category title
            if(categoryName == null || categoryName.length() == 0 || !categoryName.matches(".*\\w.*")) {
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Enter a valid category name!");
                return Response.ok(jsonArray.put(jsonObject).toString()).build();
            }
            
            category = new Category(catId, categoryName);
            int http_code = daoCategory.updateCategory(category);
            
            if(http_code == 200) {
                
                jsonObject.put("HTTP_CODE", "200");
                jsonObject.put("MSG", "Category has been updated successfully!");
                returnString = jsonArray.put(jsonObject).toString();
                
            } else {
                //return Response.status(500).entity("Unable to process add category").build();
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Category was not updated!");
                returnString = jsonArray.put(jsonObject).toString();
            }
            
        } catch (Exception e) {
            //return Response.status(500).entity("Server unable to process request.").build();
            jsonObject.put("HTTP_CODE", "500");
            jsonObject.put("MSG", "Server unable to process update request!");
            returnString = jsonArray.put(jsonObject).toString();
        }
                
        return Response.ok(returnString).build();
    }
    
    /**
     * This method will allow you to delete data in the category table.
     * In this example we are using both PathParams and the message body (payload).
     * @param categoryInfo
     * @return 
     * @throws java.lang.Exception
     */
    @DELETE
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(String categoryInfo) throws Exception {
        
        String categoryId;
        DAOCategory daoCategory = new DAOCategory();
        String returnString;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            
            JSONObject partsData = new JSONObject(categoryInfo);
            categoryId = partsData.optString("category_id");
            
            int catId;
            try {
                catId = Integer.parseInt(categoryId);
            } catch (NumberFormatException e) {
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Category id is not valid!");
                return Response.ok(jsonArray.put(jsonObject).toString()).build();
            }
            
            int http_code = daoCategory.deleteCategory(catId);
            
            if(http_code == 200) {
                
                jsonObject.put("HTTP_CODE", "200");
                jsonObject.put("MSG", "Category has been deleted successfully!");
                returnString = jsonArray.put(jsonObject).toString();
                
            } else {
                //return Response.status(500).entity("Unable to process add category").build();
                jsonObject.put("HTTP_CODE", "500");
                jsonObject.put("MSG", "Category was not deleted!");
                returnString = jsonArray.put(jsonObject).toString();
            }
            
        } catch (Exception e) {
            //return Response.status(500).entity("Server unable to process request.").build();
            jsonObject.put("HTTP_CODE", "500");
            jsonObject.put("MSG", "Server unable to process delete request!");
            returnString = jsonArray.put(jsonObject).toString();
        }
                
        return Response.ok(returnString).build();
    }  
}
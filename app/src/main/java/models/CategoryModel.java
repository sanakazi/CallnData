package models;

/**
 * Created by Callndata on 4/22/2016.
 */
public class CategoryModel {
    int categoryId;
    String cat_fullName;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCat_fullName() {
        return cat_fullName;
    }

    public void setCat_fullName(String cat_fullName) {
        this.cat_fullName = cat_fullName;
    }
}

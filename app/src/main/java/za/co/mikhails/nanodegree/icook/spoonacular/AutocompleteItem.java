package za.co.mikhails.nanodegree.icook.spoonacular;

public class AutocompleteItem {
    String name;
    String image;

    public AutocompleteItem(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public AutocompleteItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

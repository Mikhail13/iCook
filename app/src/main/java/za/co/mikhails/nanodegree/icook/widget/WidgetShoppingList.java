package za.co.mikhails.nanodegree.icook.widget;

public class WidgetShoppingList {
    String name;
    String image;
    String amount;
    String unit;
    boolean checked;

    public WidgetShoppingList(String name, String image, String amount, String unit, boolean checked) {
        this.amount = amount;
        this.checked = checked;
        this.image = image;
        this.name = name;
        this.unit = unit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

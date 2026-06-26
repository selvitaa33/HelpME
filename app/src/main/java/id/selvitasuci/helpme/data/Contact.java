package id.selvitasuci.helpme.data;

public class Contact {
    private String name;
    private String phone;
    private String relation;

    public Contact(String name, String phone, String relation) {
        this.name = name;
        this.phone = phone;
        this.relation = relation;
    }

    public String getName()     { return name; }
    public String getPhone()    { return phone; }
    public String getRelation() { return relation; }

    public void setName(String name)         { this.name = name; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setRelation(String relation) { this.relation = relation; }
}

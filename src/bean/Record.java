package bean;

/**
 * Created by Tagirov on 26.06.2018.
 */
public class Record implements Comparable {
    private int id;
    private String name;
    private int status;
    private String definition;
    private String fullDescription;

    public Record(int id, String name, int status, String definition, String fullDescription) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.definition = definition;
        this.fullDescription = fullDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    @Override
    public int compareTo(Object o) {
        return this.getName().toLowerCase().compareTo(((Record) o).getName().toLowerCase());
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", definition='" + definition + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                '}';
    }
}

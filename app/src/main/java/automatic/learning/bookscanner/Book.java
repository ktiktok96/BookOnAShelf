package automatic.learning.bookscanner;

public class Book implements Comparable<Book> {
    private float barcode_id;
    private float isbn_13;
    private String isbn_10;
    private String title;
    private String author;
    private String publisher;
    private String publishing_date;



    private String thumbnail;
    private double page;
    private String description;


    private String buyLink;


    public Book(float barcode_id, float isbn_13, String isbn_10, String title, String author, String publisher, String publishing_date, double page, String thumb, String description, String buyLink) {
        this.barcode_id = barcode_id;
        this.isbn_13 = isbn_13;
        this.isbn_10 = isbn_10;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishing_date = publishing_date;
        this.page = page;
        this.thumbnail = thumb;
        this.description = description;
        this.buyLink = buyLink;
    }



    public float getBarcode_id() {
        return barcode_id;
    }
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public void setBarcode_id(float barcode_id) {
        this.barcode_id = barcode_id;
    }

    public float getIsbn_13() {
        return isbn_13;
    }

    public void setIsbn_13(float isbn_13) {
        this.isbn_13 = isbn_13;
    }

    public String getIsbn_10() {
        return isbn_10;
    }

    public void setIsbn_10(String isbn_10) {
        this.isbn_10 = isbn_10;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishing_date() {
        return publishing_date;
    }

    public void setPublishing_date(String publishing_date) {
        this.publishing_date = publishing_date;
    }

    public double getPage() {
        return page;
    }

    public void setPage(double weight) {
        this.page = weight;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }


    @Override
    public int compareTo(Book o) {
        return this.title.compareTo(o.getTitle());
    }
}

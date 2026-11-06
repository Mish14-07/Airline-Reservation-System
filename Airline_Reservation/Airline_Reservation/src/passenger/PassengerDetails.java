
package passenger;
public class PassengerDetails {

    private String givenName;
    private String surname;
    private String birth;
    private String passportNum;
    private String email;
    private String contactNum;
    private String gender;
    private String passengerId = "";

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNum() {
        return contactNum;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

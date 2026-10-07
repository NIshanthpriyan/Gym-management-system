# 🍃 MongoDB Atlas Connection Setup Guide

இந்த Gym Management System திட்டத்தை **MongoDB Atlas (Cloud Database)** உடன் இணைப்பதற்கான எளிய வழிகாட்டி:

---

### 📍 1. உங்கள் Atlas Connection String-ஐ எங்கு சேர்க்க வேண்டும்?

கோப்பு பாதை: **[src/main/resources/application.properties](file:///c:/Users/NISHANTH%20PRIYAN.P/OneDrive/Desktop/Gym%20management%20system/src/main/resources/application.properties)**

Line 6-ல் உள்ள `spring.data.mongodb.uri`-ல் உங்கள் Atlas link-ஐ paste செய்யவும்:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster-url>/gym_management_db?retryWrites=true&w=majority
```

---

### 🔑 2. Example (உதாரணம்):

உங்கள் MongoDB Atlas username `admin`, password `mypassword123`, cluster `cluster0.abcde.mongodb.net` என்றால்:

```properties
spring.data.mongodb.uri=mongodb+srv://admin:mypassword123@cluster0.abcde.mongodb.net/gym_management_db?retryWrites=true&w=majority
```

> **முக்கிய குறிப்பு:**
> 1. `<username>` மற்றும் `<password>`-க்கு பதிலாக உங்கள் உண்மையான Database User username & password-ஐ கொடுக்கவும் (Account password அல்ல).
> 2. Password-ல் special characters (`@`, `:`, `/` போன்றவை) இருந்தால் [URL encode](https://www.urlencoder.org/) செய்து பயன்படுத்தவும் (எ.கா: `@` = `%40`).
> 3. URL-ன் முடிவில் database பெயராக `/gym_management_db` இருப்பதை உறுதிசெய்யவும்.

---

### 🌐 3. MongoDB Atlas-ல் செய்ய வேண்டியவை (Checklist):

1. **Network Access (IP Whitelist):**
   - Atlas Dashboard -> **Network Access** -> **Add IP Address** -> **Allow Access From Anywhere (`0.0.0.0/0`)** சேர்க்கவும்.
2. **Database User Permissions:**
   - Atlas Dashboard -> **Database Access** -> உங்கள் User-க்கு **Read and write to any database** permission இருப்பதை உறுதி செய்யவும்.

---

### 🚀 4. திட்டத்தை இயக்குவது எப்படி (Run Application):

Connection string paste செய்த பிறகு, [run-app.bat](file:///c:/Users/NISHANTH%20PRIYAN.P/OneDrive/Desktop/Gym%20management%20system/run-app.bat) கோப்பை இயக்கவும் அல்லது Terminal-ல்:

```powershell
.\temp_maven\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```

Backend start ஆனதும், MongoDB Atlas Cloud-ல் தானாகவே `gym_management_db` database மற்றும் 15 collections create ஆகி initial data seed ஆகிவிடும்!

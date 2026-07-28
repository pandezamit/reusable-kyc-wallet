

As an AI, I cannot host files on a live web server to provide a direct clickable download link. However, I can provide you with a **single automated bash script** that will instantly recreate the entire project structure, files, and codebase directly on your local machine.

### How to get and run the project locally:

1. Save the following code block into a file named `setup-kyc-repo.sh` on your machine.
2. Make it executable: `chmod +x setup-kyc-repo.sh`
3. Run it: `./setup-kyc-repo.sh`


### Prerequisites to Run Locally:
*   **Java 17** (Ensure `JAVA_HOME` is set)
*   **Maven** (For building the Spring Boot application)
*   **Docker & Docker Compose** (If you want to spin up the PostgreSQL, MinIO, and Canton nodes via the `docker-compose.yml` file)

Once you run the script, you can simply `cd reusable-kyc-wallet/backend` and run `mvn spring-boot:run` to start the application immediately.

### 1. Ensure you are in the `backend` directory
You must navigate into the `backend` folder where the `pom.xml` file is located before running the Maven command:

```bash
cd reusable-kyc-wallet/backend
```

### 2. Verify the `pom.xml` exists
Check that the `pom.xml` file is actually in that directory:
```bash
ls -l pom.xml
# or on Windows:
dir pom.xml
```

### 3. Run the application
Once you are inside the `backend` directory, run:
```bash
mvn spring-boot:run
```

### 4. Alternative approach (if the error persists)
If you are definitely in the `backend` folder and still see this error, force Maven to download the plugin and build the project first:
```bash
mvn clean install
mvn spring-boot:run
```

### Windows PowerShell Note:
If you are using Windows PowerShell, `mvn` might act up with the `spring-boot:run` prefix. You can bypass this by running:
```powershell
mvn org.springframework.boot:spring-boot-maven-plugin:run
``` 

Once it successfully starts, you will see the Spring Boot banner in your console, and you can access the API at `http://localhost:8080/swagger-ui.html`.


To create an interactive web-based application that scans documents via a webcam and submits them to your Spring Boot backend, we can use standard HTML5, JavaScript (using `navigator.mediaDevices.getUserMedia` for webcam access), and CSS.

Spring Boot automatically serves static files placed in the `src/main/resources/static` folder. 

### 1. Create the Frontend Application

Create a new file named `index.html` inside your backend project at this exact path:
`reusable-kyc-wallet/backend/src/main/resources/static/index.html`



### 2. Run the Application

1. Ensure you are in the `backend` directory.
2. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser and navigate to:
   **`http://localhost:8080/`**

### How it works:
1. **Webcam Access**: The app uses the native browser API `navigator.mediaDevices.getUserMedia` to request access to your webcam.
2. **Capture Document**: When you click "Capture Document", it draws the current video frame onto an HTML5 `<canvas>`, stops the webcam stream, and converts the canvas drawing into a JPEG `Blob`.
3. **Multipart Form Submission**: It packages your `customerId` and `did` into a JSON `Blob`, appends both the JSON and the Image Blob to a `FormData` object, and POSTs it to the backend at `/api/v1/kyc/initiate`.
4. **Backend Processing**: The Spring Boot backend receives the file, saves it to the mock S3 storage, computes the hash, calls the mock Mitek API, and writes the mock Canton Smart Contract.
5. **Result Display**: The UI displays the resulting Canton Contract ID, Mitek Dossier ID, and Verification Status directly to the user.
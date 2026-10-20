# Recipe Planner – Unit Testing, Reports & Pipeline

| Aufgabe | Doku | Was zeigen |
|---|---|---|
| 1 – Unit Testing | [aufgabe-1/](aufgabe-1/README.md) | 3 Testklassen (Controller + 2 Mapper) |
| 2 – Reports | [aufgabe-2/](aufgabe-2/README.md) | Surefire- und JaCoCo-Report im Browser |
| 3 – Pipeline | [aufgabe-3/](aufgabe-3/README.md) | GitHub Actions Lauf mit Report |

## Applikation starten

**Backend** (Port 8080):

```bash
cd recipe-planner/recipe-planner-backend
./mvnw spring-boot:run
```

**Frontend** (Port 3000):

```bash
cd recipe-planner/recipe-planner-fronend
npm install
npm start
```

**Anpassungen am Projekt:**
- Maven Wrapper (`mvnw`) hinzugefügt, er fehlte im ZIP
- `pom.xml`: Surefire-, Surefire-Report- und JaCoCo-Plugin ergänzt
- `lombok.config`: von Lombok generierter Code (Getter, Setter …) wird nicht in die Coverage gezählt

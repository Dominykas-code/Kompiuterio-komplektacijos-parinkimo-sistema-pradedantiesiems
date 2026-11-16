# Kompiuterio komplektacijos parinkimo sistema pradedantiesiems

**Programų sistemų projektavimas – II etapas: veikiantis prototipas**

Projektas sudaro kompiuterio komplektaciją pagal biudžetą ir paskirtį, patikrina techninį suderinamumą, apskaičiuoja balus, parodo alternatyvą konfliktiniam pasirinkimui ir izoliuoja dviejų demonstracinių organizacijų duomenis. AI tik paaiškina **jau patikrintą** sprendimą; galutinių suderinamumo sprendimų nepriima.


## Reikalinga aplinka

- **Java JDK 21**, pvz., Eclipse Temurin.
- **IntelliJ IDEA** – gali atidaryti `backend/pom.xml` kaip Maven projektą.
- **Apache Maven 3.9+** (arba su IntelliJ IDEA integruotas Maven).
- **Spring Boot 4.1.2** (automatiškai atsisiunčiamas per Maven).
- **H2** (automatiškai atsisiunčiama per Maven; atskiros DB diegti nereikia).
- **React 16.0.0 ir ReactDOM 16.0.1** lokaliai įdėti į `static/vendor/` – prototipui nereikia Node.js ar npm. Tai tik lokalus mokomasis demonstracinis UI; produkcijai reikėtų naujesnės palaikomos React versijos.
- Pirmą kartą paleidžiant reikia **interneto ryšio**, kad Maven atsisiųstų Spring Boot priklausomybes.


   ```cmd
   start.cmd
   ```

Jei `start.cmd` praneša, kad Maven nerastas, IntelliJ kairėje atidaryk `backend/src/main/java/lt/pcbuilder/PcbuilderApplication.java` ir spausk žalią ▶ prie `main`. Tai lygiavertis paleidimas, naudojantis IDE integruotu Maven.
http://localhost:8080**. Jei matai kompiuterio parinkimo formą – serveris veikia.
http://localhost:8080/api/health** → `{"status":"ok"}`.

Komandinės eilutės alternatyva, kai `mvn` įdiegtas:

```cmd
cd backend
mvn spring-boot:run
```



## Testų paleidimas

Iš projekto šakninio aplanko:

```cmd
test.cmd
```

Arba IDE išskleisk `backend/src/test/java` ir paleisk visus testus. Kai Maven pasiekiamas, analogiškai:

```cmd
cd backend
mvn test
```

**Papildomas nuo išorinių priklausomybių nepriklausomas parinkimo logikos patikrinimas** (`javac` + `java`):

```cmd
scripts\check-core.cmd
```

Pagrindiniai JUnit testai yra `BuildPlannerTest` (sėkmingas, konfliktinis, klaidos, turimo SSD atvejai), `TenantIsolationTest` (vieno kliento draudžiami GET ir PATCH kito kliento įrašui) ir `AiOutputPolicyTest` (AI negali rekomenduoti nepatvirtinto ID). Minimalūs keturi reikalaujami scenarijai padengti.

## Demonstraciniai klientai

| Organizacija | HTTP antraštė `X-Demo-Key` | Pavyzdinės komplektacijos ID |
|---|---|---|
| PCStart | `pcstart-demo-key` | `demo-pcstart-001` |
| BuildLab | `buildlab-demo-key` | `demo-buildlab-001` |

UI leidžia pasirinkti klientą. Serveris nustato kliento kontekstą pagal pateiktą demonstracinį raktą, **o ne laisvai vartotojo sugalvotą `tenantId`**. Visos katalogų bei išsaugotų komplektacijų užklausos apribotos kliento kontekstu. Svetimo įrašo nuskaitymas **ir redagavimas** grąžina HTTP 404. Su netinkamu raktu – HTTP 401.

**Saugumo ribojimas:** šie raktai pateikti README ir frontend'e tik demonstracijai. Jie **nėra produkcijai tinkama autentifikacija**. 


## AI integracija (pasirenkama lokalioje aplinkoje)

Naudojama oficialaus OpenAI Responses API užklausa per Java `HttpClient`, tik jei nustatytas aplinkos kintamasis. AI gauna **jau patikrintos** komplektacijos faktus ir leidžiamą alternatyvą. Atsakymo `suggestedComponentId` tikrinamas pagal parinktų komponentų ID ir patvirtintą pakeitimą. Netinkama rekomendacija ar API klaida atmetama, o naudotojui pateikiamas deterministinis paaiškinimas.

Niekada nekelk API rakto į GitHub ar į `application.properties`! CMD lange, kuriame paleidi projektą:

```cmd
set OPENAI_API_KEY=cia_ivesk_savo_rakta_lokaliai
set OPENAI_MODEL=gpt-4.1-mini
start.cmd
```

Be rakto prototipas paleidžiamas be AI paslaugos, bet **I etape suplanuotos realios AI integracijos gyvam demonstravimui reikia veikiančio API rakto, interneto ir paslaugos prieigos**. Atsakymų iš API gavimas gali priklausyti nuo paskyros ir paslaugos būklės. Integracijos išoriniu servisu šiame aplinkoje nepavyko patikrinti.

## API

| Metodas | Kelias | Rezultatas |
|---|---|---|
| GET | `/api/health` | Serverio būsena |
| GET | `/api/catalog` | Tik aktyvaus kliento komponentai |
| POST | `/api/builds` | Parenka, patikrina, išsaugo ir paaiškina komplektaciją |
| GET | `/api/builds` | Tik aktyvaus kliento komplektacijų sąrašas |
| GET | `/api/builds/{id}` | Tik aktyvaus kliento įrašas |
| PATCH | `/api/builds/{id}` | Tik aktyvaus kliento įrašo pavadinimo keitimas |
| GET | `/api/tenant` | Kuriam klientui priskirtas demo raktas |

Visiems, išskyrus health, būtina antraštė `X-Demo-Key`.

Pavyzdys PowerShell:

```powershell
Invoke-RestMethod 'http://localhost:8080/api/builds' -Method Post `
  -Headers @{'X-Demo-Key'='pcstart-demo-key'} -ContentType 'application/json' `
  -Body '{"budgetEur":1000,"purpose":"gaming","label":"Testas","ownedPartIds":[]}'
```



## Verslo taisyklės

1. Procesoriaus ir pagrindinės plokštės `socket` sutampa, RAM tipas atitinka plokštę, korpusas talpina pagrindinę plokštę bei GPU.
2. PSU nominali galia bent **120 %** apskaičiuotos visų dalių galios sumos. Šie demonstraciniai galios skaičiavimai – supaprastinta I etapo prielaida, ne realus saugumo ar tinkamumo sertifikavimas.
3. Komponentai telpa į biudžetą (turimam SSD taikoma 0 € mokėtina kaina).
4. Parenkama didžiausio balo komplektacija; jei balai vienodi – pigesnė. Žaidimams: GPU **50 %**, CPU **30 %**, RAM **10 %**, SSD **10 %**. Kitos paskirtys turi atskirus svorius.
5. Netinkamas pageidaujamas komponentas pakeičiamas **tik patikrintu** tos pačios kategorijos komponentu (jei galioja visi apribojimai), o ne išgalvotu pasiūlymu.



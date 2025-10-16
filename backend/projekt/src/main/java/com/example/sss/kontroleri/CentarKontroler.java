package com.example.sss.kontroleri;

import com.example.sss.model.*;
import com.example.sss.model.DTO.*;
import com.example.sss.model.elasticsearch.CentarDocument;
import com.example.sss.repozitorijumi.*;
import com.example.sss.servisi.CentarServis;
import com.example.sss.servisi.DostupnostServis;
import com.example.sss.servisi.RadnoVremeServis;
import com.example.sss.servisi.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Time;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/centri")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"}, 
             allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CentarKontroler {

    @Autowired
    CentarServis centarServis;

    @Autowired
    DostupnostServis dostupnostServis;

    @Autowired
    RadnoVremeServis radnoVremeServis;

    @Autowired
    CentarRepozitorijum centarRepozitorijum;

    @Autowired
    DisciplinaRepozitorijum disciplinaRepozitorijum;

    @Autowired
    DostupnostRepozitorijum dostupnostRepozitorijum;

    @Autowired
    KorisnikRepozitorijum korisnikRepozitorijum;

    @Autowired
    RezervacijaRepozitorijum rezervacijaRepozitorijum;

    @Autowired
    RadnoVremeRepozitorijum radnoVremeRepozitorijum;

    @Autowired
    OcenaRepozitorijum ocenaRepozitorijum;

    TokenUtils tokenUtils = new TokenUtils();

    @GetMapping("sve")
    public ResponseEntity<List<CentarDTO>> getAllCentri() {
        List<Centar> centri = centarServis.getAll();
        List<CentarDTO> centarDTOS = new ArrayList<>();

        for (Centar centar : centri) {
            CentarDTO centarDTO = new CentarDTO();
            centarDTO.setIme(centar.getIme());
            centarDTO.setOphis(centar.getOphis());
            centarDTO.setDatumKreacije(centar.getDatumKreacije());
            centarDTO.setAdresa(String.valueOf(centar.getAdresa()));
            centarDTO.setGrad(String.valueOf(centar.getGrad()));
            centarDTO.setRating(centar.getRating());
            System.out.println(centarDTO.getIme());
            centarDTOS.add(centarDTO);
        }

        return ResponseEntity.ok(centarDTOS);
    }

    @GetMapping("/pretraga")
    public ResponseEntity<List<CentarDTO>> pretraga(
            @RequestParam(value = "grad", required = false) String grad,
            @RequestParam(value = "ponedeljak", required = false) List<String> ponedeljak,
            @RequestParam(value = "utorak", required = false) List<String> utorak,
            @RequestParam(value = "sreda", required = false) List<String> sreda,
            @RequestParam(value = "cetvrtak", required = false) List<String> cetvrtak,
            @RequestParam(value = "petak", required = false) List<String> petak,
            @RequestParam(value = "subota", required = false) List<String> subota,
            @RequestParam(value = "nedelja", required = false) List<String> nedelja,
            @RequestParam(value = "ocenamin", required = false) String ocenamin,
            @RequestParam(value = "ocenamax", required = false) String ocenamax,
            @RequestParam(value = "disciplina", required = false) List<String> discipline,
            @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        }
        catch (Exception ignored){

        }

        if(email == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        if (grad == null || grad.isEmpty()) {
            grad = "%";
        }
        System.out.println(grad);

        if (ponedeljak != null) {
            System.out.println("bruhimics");
            for (String radnovreme : ponedeljak) {
                String[] sati = radnovreme.split("-");
                System.out.println("bruhimics22");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    ponedeljak = null;
                }
            }
        }

        if (utorak != null) {
            for (String radnovreme : utorak) {
                String[] sati = radnovreme.split("-");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    utorak = null;
                }
            }
        }

        if (sreda != null) {
            for (String radnovreme : sreda) {
                String[] sati = radnovreme.split("-");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    sreda = null;
                }
            }
        }

        if (cetvrtak != null) {
            for (String radnovreme : cetvrtak) {
                String[] sati = radnovreme.split("-");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    cetvrtak = null;
                }
            }
        }

        if (petak != null) {
            for (String radnovreme : petak) {
                String[] sati = radnovreme.split("-");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    petak = null;
                }
            }
        }

        if (subota != null) {
            for (String radnovreme : subota) {
                String[] sati = radnovreme.split("-");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    subota = null;
                }
            }
        }

        if (nedelja != null) {
            for (String radnovreme : nedelja) {
                String[] sati = radnovreme.split("-");
                try {
                    Time.valueOf(sati[0]);
                    Time.valueOf(sati[1]);
                } catch (Exception e) {
                    System.out.println("AAAAAAAAAAAAAAA");
                    nedelja = null;
                }
            }
        }

        if (ocenamin == null || ocenamin.isEmpty()) {
            ocenamin = "0";
        } else {
            try {
                Double.parseDouble(ocenamin);
            } catch (NumberFormatException e) {
                ocenamin = "0";
            }
        }

        if (ocenamax == null || ocenamax.isEmpty()) {
            ocenamax = "11";
        } else {
            try {
                Double.parseDouble(ocenamax);
            } catch (NumberFormatException e) {
                ocenamax = "11";
            }
        }

        if (discipline == null || discipline.isEmpty()) {
            discipline = new ArrayList<>();
        }

        if (ponedeljak == null || ponedeljak.isEmpty()) {
            ponedeljak = new ArrayList<>();
        }

        if (utorak == null || utorak.isEmpty()) {
            utorak = new ArrayList<>();
        }

        if (sreda == null || sreda.isEmpty()) {
            sreda = new ArrayList<>();
        }

        if (cetvrtak == null || cetvrtak.isEmpty()) {
            cetvrtak = new ArrayList<>();
        }

        if (petak == null || petak.isEmpty()) {
            petak = new ArrayList<>();
        }

        if (subota == null || subota.isEmpty()) {
            subota = new ArrayList<>();
        }

        if (nedelja == null || nedelja.isEmpty()) {
            nedelja = new ArrayList<>();
        }

        for (int m = 0; m < 10; m++) {
            System.out.println("!!!!!!!!!!!!!!!!");
        }

        System.out.println(grad + ocenamin + ocenamax + discipline);

        List<Integer> otvoreniCentri = radnoVremeServis.nadjiOtvoreneCentre(ponedeljak, utorak, sreda, cetvrtak, petak, subota, nedelja);

        List<Disciplina> pretragaDisciplina = disciplinaRepozitorijum.nadjiDiscipline(discipline);
        for (Disciplina disciplina : pretragaDisciplina) {
            System.out.println(disciplina.ime);
        }
        List<Integer> disciplineIds = pretragaDisciplina.stream()
                .map(Disciplina::getId)
                .collect(Collectors.toList());
        List<Integer> centriSaDatimDisciplinama = dostupnostServis.nadjiCentreSaDatimDisciplinama(disciplineIds);

        List<Centar> centri;
        if(discipline.isEmpty() && ponedeljak.isEmpty() && utorak.isEmpty() && sreda.isEmpty() && cetvrtak.isEmpty() && petak.isEmpty() && subota.isEmpty() && nedelja.isEmpty()) {
            centri = centarRepozitorijum.filterBezDisciplina(grad, Integer.parseInt(ocenamin), Integer.parseInt(ocenamax));
        }
        else if (ponedeljak.isEmpty() && utorak.isEmpty() && sreda.isEmpty() && cetvrtak.isEmpty() && petak.isEmpty() && subota.isEmpty() && nedelja.isEmpty()) {
            centri = centarRepozitorijum.filter(grad, Integer.parseInt(ocenamin), Integer.parseInt(ocenamax), centriSaDatimDisciplinama);
        }
        else if (discipline.isEmpty()) {
            centri = centarRepozitorijum.filter(grad, Integer.parseInt(ocenamin), Integer.parseInt(ocenamax), otvoreniCentri);
        }
        else {
            Set<Integer> faszok = new HashSet<>(otvoreniCentri);
            faszok.retainAll(centriSaDatimDisciplinama);
            List<Integer> ranni = new ArrayList<>(faszok);
            System.out.println("ZLATAAAAAAAAAAN");
            centri = centarRepozitorijum.filter(grad, Integer.parseInt(ocenamin), Integer.parseInt(ocenamax), ranni);
        }

        List<CentarDTO> centarDTOS = new ArrayList<>();

        for (Centar centar : centri) {
            CentarDTO centarDTO = new CentarDTO();
            centarDTO.setId(centar.getId());
            centarDTO.setIme(centar.getIme());
            centarDTO.setOphis(centar.getOphis());
            centarDTO.setDatumKreacije(centar.getDatumKreacije());
            centarDTO.setAdresa(String.valueOf(centar.getAdresa()));
            centarDTO.setGrad(String.valueOf(centar.getGrad()));
            if (centar.getRating() != 11) {
                centarDTO.setRating(centar.getRating());
            }
            
            // Add image and PDF URLs
            if (centar.getImagePath() != null && !centar.getImagePath().isEmpty()) {
                centarDTO.setImagePath("/api/centri/" + centar.getId() + "/image");
            }
            
            if (centar.getPdfPath() != null && !centar.getPdfPath().isEmpty()) {
                centarDTO.setPdfPath("/api/centri/" + centar.getId() + "/pdf");
                centarDTO.setHasPdf(true);
            } else {
                centarDTO.setHasPdf(false);
            }
            
            System.out.println(centarDTO.getOphis());
            centarDTOS.add(centarDTO);
        }

        return ResponseEntity.ok(centarDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CentarDTO2> getCentar(@PathVariable String id, @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        }
        catch (Exception ignored){

        }

        if(email == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            Integer.parseInt(id);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Centar centar = centarRepozitorijum.findById(Integer.parseInt(id));

        if(centar != null) {
            if (centar.isActive()) {
                CentarDTO2 centarDTO = new CentarDTO2();
                centarDTO.setId(centar.getId());
                centarDTO.setIme(centar.getIme());
                centarDTO.setOphis(centar.getOphis());
                centarDTO.setDatumKreacije(centar.getDatumKreacije());
                centarDTO.setAdresa(String.valueOf(centar.getAdresa()));
                centarDTO.setGrad(String.valueOf(centar.getGrad()));
                if (centar.getRating() != 11) {
                    centarDTO.setRating(centar.getRating());
                }
                
                // Set review count
                int reviewCount = ocenaRepozitorijum.oceneObjekta(centar.getId()).size();
                centarDTO.setReviewCount(reviewCount);
                
                System.out.println(centarDTO.getIme());

                List<RadnoVreme> radnaVremena = radnoVremeRepozitorijum.otvoren(centar.getId(), "ponedeljak");
                List<RadnoVreme> radnaVremena1 = radnoVremeRepozitorijum.otvoren(centar.getId(), "utorak");
                List<RadnoVreme> radnaVremena2 = radnoVremeRepozitorijum.otvoren(centar.getId(), "sreda");
                List<RadnoVreme> radnaVremena3 = radnoVremeRepozitorijum.otvoren(centar.getId(), "cetvrtak");
                List<RadnoVreme> radnaVremena4 = radnoVremeRepozitorijum.otvoren(centar.getId(), "petak");
                List<RadnoVreme> radnaVremena5 = radnoVremeRepozitorijum.otvoren(centar.getId(), "subota");
                List<RadnoVreme> radnaVremena6 = radnoVremeRepozitorijum.otvoren(centar.getId(), "nedelja");
                List<RadnoVreme> allRadnaVremena = new ArrayList<>();
                allRadnaVremena.addAll(radnaVremena);
                allRadnaVremena.addAll(radnaVremena1);
                allRadnaVremena.addAll(radnaVremena2);
                allRadnaVremena.addAll(radnaVremena3);
                allRadnaVremena.addAll(radnaVremena4);
                allRadnaVremena.addAll(radnaVremena5);
                allRadnaVremena.addAll(radnaVremena6);

                List<RadnoVremeDTO> radnoVremeDTOi = new ArrayList<>();
                for (RadnoVreme radnoVreme : allRadnaVremena) {
                    System.out.println(radnoVreme.getDanNedelje());
                    RadnoVremeDTO radnoVremeDTO = new RadnoVremeDTO();
                    radnoVremeDTO.danNedelje = String.valueOf(radnoVreme.getDanNedelje());
                    radnoVremeDTO.vremeOtvaranja = radnoVreme.getVremeOtvaranja();
                    radnoVremeDTO.vremeZatvaranja = radnoVreme.getVremeZatvaranja();
                    radnoVremeDTOi.add(radnoVremeDTO);
                }
                centarDTO.setRadnoVremeDTOList(radnoVremeDTOi);

                List<Dostupnost> dostupnosti = dostupnostRepozitorijum.nadjiDisciplineCentra(centar.getId());
                centarDTO.discipline = new ArrayList<>();
                for (Dostupnost dostupnost : dostupnosti) {
                    centarDTO.discipline.add(dostupnost.getDisciplina().ime);
                }

                // Add image and PDF URLs
                if (centar.getImagePath() != null && !centar.getImagePath().isEmpty()) {
                    centarDTO.setImagePath("/api/centri/" + centar.getId() + "/image");
                }
                
                if (centar.getPdfPath() != null && !centar.getPdfPath().isEmpty()) {
                    centarDTO.setPdfPath("/api/centri/" + centar.getId() + "/pdf");
                    centarDTO.setHasPdf(true);
                } else {
                    centarDTO.setHasPdf(false);
                }

                return new ResponseEntity<>(centarDTO, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/novicentar")
    public ResponseEntity<CentarDTO> createCentar(
            HttpServletRequest request,
            @RequestParam(value = "ime", required = false) String ime,
            @RequestParam(value = "ophis", required = false) String ophis,
            @RequestParam(value = "adresa", required = false) String adresa,
            @RequestParam(value = "grad", required = false) String grad,
            @RequestParam(value = "discipline", required = false) List<String> discipline,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "pdf", required = false) MultipartFile pdf,
            @RequestHeader("authorization") String token) {

        log.info("=== CREATE CENTAR REQUEST ===");
        log.info("Content-Type: {}", request.getContentType());
        log.info("Method: {}", request.getMethod());
        log.info("Parameter names: {}", Collections.list(request.getParameterNames()));
        log.info("ime: {}", ime);
        log.info("ophis: {}", ophis);
        log.info("adresa: {}", adresa);
        log.info("grad: {}", grad);
        log.info("discipline: {}", discipline);
        log.info("Files provided - Image: {}, PDF: {}", image != null && !image.isEmpty(), pdf != null && !pdf.isEmpty());
        
        // Validate required fields
        if (ime == null || ophis == null || adresa == null || grad == null || discipline == null || discipline.isEmpty()) {
            log.error("Missing required fields!");
            return ResponseEntity.badRequest().build();
        }

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        }
        catch (Exception e){
            log.error("Error extracting email from token", e);
        }

        if(email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.ADMIN) {
                    // Create the center in database
                    centarRepozitorijum.insert(ime, ophis, LocalDateTime.now(), adresa, grad);
                    int sui = centarRepozitorijum.getLastInsertedId();
                    log.info("Centar created with ID: {}", sui);
                    
                    // Add disciplines
                    List<Disciplina> pretragaDisciplina = disciplinaRepozitorijum.nadjiDiscipline(discipline);
                    for (Disciplina disciplina : pretragaDisciplina) {
                        log.debug("Adding disciplina: {}", disciplina.ime);
                    }
                    List<Integer> disciplineIds = pretragaDisciplina.stream()
                            .map(Disciplina::getId)
                            .collect(Collectors.toList());
                    for (Integer i : disciplineIds) {
                        dostupnostRepozitorijum.insert(sui, i);
                    }

                    // Upload files to MinIO if provided
                    try {
                        if ((image != null && !image.isEmpty()) || (pdf != null && !pdf.isEmpty())) {
                            log.info("Uploading files for centar ID: {}", sui);
                            centarServis.uploadFiles(sui, image, pdf);
                            log.info("Files uploaded successfully and indexed in Elasticsearch for centar ID: {}", sui);
                        } else {
                            log.info("No files to upload for centar ID: {}, indexing basic info only", sui);
                            // Index center without files
                            Centar newCentar = centarRepozitorijum.findById(sui);
                            if (newCentar != null) {
                                centarServis.indexCentar(newCentar);
                                log.info("Centar indexed successfully in Elasticsearch: {}", sui);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Failed to upload files or index center: " + e.getMessage(), e);
                        // Try to index basic info even if file upload fails
                        try {
                            Centar newCentar = centarRepozitorijum.findById(sui);
                            if (newCentar != null) {
                                centarServis.indexCentar(newCentar);
                            }
                        } catch (Exception ex) {
                            log.error("Failed to index center after file upload error", ex);
                        }
                    }

                    return new ResponseEntity<>(null, HttpStatus.OK);
                }
            }
        }

        log.warn("Unauthorized attempt to create centar");
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/discipline")
    public ResponseEntity<List<Disciplina>> sveDiscipline() {
        List<Disciplina> discipline = disciplinaRepozitorijum.sveDiscipline();

        return ResponseEntity.ok(discipline);
    }

    @PostMapping("/izmenicentar")
    public ResponseEntity<CentarDTO> izmeniCentar(@RequestParam(value = "id", required = true) String id,
                                                  @RequestParam(value = "ime", required = false) String ime,
                                                  @RequestParam(value = "ophis", required = false) String ophis,
                                                  @RequestParam(value = "adresa", required = false) String adresa,
                                                  @RequestParam(value = "grad", required = false) String grad,
                                                  @RequestParam(value = "disciplina", required = false) List<String> discipline,
                                                  @RequestParam(value = "obrisi", required = false) String obrisi,
                                                  @RequestHeader("authorization") String token) {

        try {
            Integer.valueOf(id);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (obrisi != null) {
            try {
                Boolean.valueOf(obrisi);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.ADMIN) {
                    Centar centar = centarRepozitorijum.findById(Integer.parseInt(id));

                    if (centar != null) {
                        if (ime != null) {
                            centarRepozitorijum.izmeniIme(ime, centar.getId());
                            centar.setIme(ime); // Update local object for re-indexing
                        }
                        if (ophis != null) {
                            centarRepozitorijum.izmeniOphis(ophis, centar.getId());
                            centar.setOphis(ophis); // Update local object for re-indexing
                        }
                        if (adresa != null) {
                            centarRepozitorijum.izmeniAdresu(adresa, centar.getId());
                            centar.setAdresa(adresa); // Update local object for re-indexing
                        }
                        if (grad != null) {
                            centarRepozitorijum.izmeniGrad(grad, centar.getId());
                            centar.setGrad(grad); // Update local object for re-indexing
                        }
                        if (discipline != null && !discipline.isEmpty()) {
                            List<Disciplina> pretragaDisciplina = disciplinaRepozitorijum.nadjiDiscipline(discipline);
                            for (Disciplina disciplina : pretragaDisciplina) {
                                System.out.println(disciplina.ime);
                            }
                            List<Integer> disciplineIds = pretragaDisciplina.stream()
                                    .map(Disciplina::getId)
                                    .collect(Collectors.toList());
                            dostupnostRepozitorijum.obrisi(centar.getId());
                            for (Integer i : disciplineIds) {
                                dostupnostRepozitorijum.insert(centar.getId(), i);
                            }
                        }
                        if (obrisi != null) {
                            centarRepozitorijum.obrisi(Boolean.parseBoolean(obrisi), centar.getId());
                            centar.setActive(!Boolean.parseBoolean(obrisi)); // Update local object for re-indexing
                        }

                        // Re-index the updated center in Elasticsearch
                        // Use the locally updated centar object which has the latest changes
                        try {
                            centarServis.indexCentar(centar);
                        } catch (Exception e) {
                            System.out.println("Failed to re-index updated center: " + e.getMessage());
                        }

                        return new ResponseEntity<>(null, HttpStatus.OK);
                    }
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/rezervisi")
    public ResponseEntity<CentarDTO> izmeniCentar(@RequestBody @Validated RezervacijaDTO rezervacijaDTO, @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        }
        catch (Exception ignored){

        }

        if(email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.KORISNIK) {
                    Instant instant = rezervacijaDTO.getDatum().toInstant();
                    ZoneId zoneId = ZoneId.systemDefault();
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
                    LocalDate localDate = localDateTime.toLocalDate(); //treba ti dan nedelje da bi skontao od kad do kad radi
                    DayOfWeek danNedelje = localDate.getDayOfWeek(); //kojim danom da ne bi mogao rezervisati van radnog vremena
                    String enumDanNedelje;
                    if (danNedelje == DayOfWeek.MONDAY) { enumDanNedelje = "ponedeljak"; }
                    else if (danNedelje == DayOfWeek.TUESDAY) { enumDanNedelje = "utorak"; }
                    else if (danNedelje == DayOfWeek.WEDNESDAY) { enumDanNedelje = "sreda"; }
                    else if (danNedelje == DayOfWeek.THURSDAY) { enumDanNedelje = "cetvrtak"; }
                    else if (danNedelje == DayOfWeek.FRIDAY) { enumDanNedelje = "petak"; }
                    else if (danNedelje == DayOfWeek.SATURDAY) { enumDanNedelje = "subota"; }
                    else if (danNedelje == DayOfWeek.SUNDAY) { enumDanNedelje = "nedelja"; }
                    else { return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); }
                    List<RadnoVreme> radnaVremena = radnoVremeRepozitorijum.otvoren(rezervacijaDTO.getCentar(), enumDanNedelje);

                    if (radnaVremena.isEmpty()) {
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }

                    if (rezervacijaDTO.pocetak.getTime() > rezervacijaDTO.kraj.getTime()) {
                        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
                    }

                    int sui = 0;
                    for (RadnoVreme radnoVreme : radnaVremena) {
                        if (rezervacijaDTO.pocetak.getTime() >= radnoVreme.getVremeOtvaranja().getTime() && rezervacijaDTO.kraj.getTime() <= radnoVreme.getVremeZatvaranja().getTime()) {
                            sui = sui + 1;
                        }
                        if (sui == 1) {
                            rezervacijaRepozitorijum.insert(rezervacijaDTO.getDatum(), rezervacijaDTO.getPocetak(), rezervacijaDTO.getKraj(), rezervacijaDTO.getCentar(), korisnik.getId());

                            return new ResponseEntity<>(null, HttpStatus.OK);
                        }
                    }

                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/novoradnovreme")
    public ResponseEntity<CentarDTO> novoRadnoVreme(@RequestBody @Validated RadnoVremeDTO radnoVremeDTO, @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        }
        catch (Exception ignored){

        }

        if(email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.ADMIN) {
                    List<RadnoVreme> radnaVremena = radnoVremeRepozitorijum.otvoren(radnoVremeDTO.centar, radnoVremeDTO.danNedelje);
                    for (RadnoVreme radnoVreme : radnaVremena) {
                        if (radnoVremeDTO.vremeZatvaranja.getTime() > radnoVreme.getVremeOtvaranja().getTime() && radnoVremeDTO.vremeOtvaranja.getTime() < radnoVreme.getVremeZatvaranja().getTime()) {
                            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                        }
                    }
                    radnoVremeRepozitorijum.novoRadnoVreme(radnoVremeDTO.danNedelje, radnoVremeDTO.vremeOtvaranja, radnoVremeDTO.vremeZatvaranja, radnoVremeDTO.centar);

                    return new ResponseEntity<>(null, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/novaocena")
    public ResponseEntity<CentarDTO> novaOcena(@RequestBody @Validated OcenaDTO ocenaDTO, @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        }
        catch (Exception ignored){

        }

        if(email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.KORISNIK) {
                    Centar centar = centarRepozitorijum.findById(ocenaDTO.centar);
                    if (centar == null) {
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                    Ocena duplikat = ocenaRepozitorijum.duplikat(centar.getId(), korisnik.getId());
                    if (duplikat != null) {
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                    ocenaRepozitorijum.insert(ocenaDTO.opremljenost, ocenaDTO.osoblje, ocenaDTO.higijena, ocenaDTO.prostorija, ocenaDTO.centar, korisnik.getId());
                    List<Ocena> oceneObjekta = ocenaRepozitorijum.oceneObjekta(ocenaDTO.centar);
                    List<Double> srednjeVrednosti = new ArrayList<>();
                    for (Ocena ocena : oceneObjekta) {
                        double sui = (double) (ocena.higijena + ocena.opremljenost + ocena.osoblje + ocena.prostorija) / 4;
                        srednjeVrednosti.add(sui);
                    }
                    double sabraneOcene = 0;
                    for (Double srednjaVrednost : srednjeVrednosti) {
                        sabraneOcene = sabraneOcene + srednjaVrednost;
                    }
                    centarRepozitorijum.izmeniOcenu(sabraneOcene/srednjeVrednosti.size(), ocenaDTO.centar);

                    // Re-index center in Elasticsearch to update reviewCount
                    try {
                        Centar updatedCentar = centarRepozitorijum.findById(ocenaDTO.centar);
                        if (updatedCentar != null) {
                            centarServis.indexCentar(updatedCentar);
                            log.info("Center {} re-indexed after new review - reviewCount updated", ocenaDTO.centar);
                        }
                    } catch (Exception e) {
                        log.error("Failed to re-index center {} after new review: {}", ocenaDTO.centar, e.getMessage());
                        // Don't fail the request if re-indexing fails
                    }

                    return new ResponseEntity<>(null, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/mojerezervacije")
    public ResponseEntity<List<RezervacijaDTO2>> sviZahtevi(@RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.KORISNIK) {
                    List<Rezervacija> rezervacije = rezervacijaRepozitorijum.mojeRezervacije(korisnik.getId());

                    List<RezervacijaDTO2> rezervacijaDTOi = new ArrayList<>();
                    for (Rezervacija rezervacija : rezervacije) {
                        RezervacijaDTO2 rezervacijaDTO = new RezervacijaDTO2();
                        rezervacijaDTO.id = rezervacija.getId();
                        rezervacijaDTO.datum = rezervacija.getDatum();
                        rezervacijaDTO.pocetak = rezervacija.getPocetak();
                        rezervacijaDTO.kraj = rezervacija.getKraj();
                        rezervacijaDTO.centar = rezervacija.getCentar().getIme();
                        System.out.println(rezervacija.getId());
                        System.out.println("ZLATAAAAAAAAAAAAAAAAAAAAAN");
                        rezervacijaDTOi.add(rezervacijaDTO);
                    }

                    return ResponseEntity.ok(rezervacijaDTOi);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/radnavremena")
    public ResponseEntity<List<RadnoVremeDTO2>> radnaVremena(@RequestParam(value = "id", required = true) String id) {
        List<RadnoVreme> radnaVremena = radnoVremeRepozitorijum.radnaVremenaZaCentar(Integer.parseInt(id));
        List<RadnoVremeDTO2> radnoVremeDTOi = new ArrayList<>();
        for (RadnoVreme radnoVreme : radnaVremena) {
            RadnoVremeDTO2 radnoVremeDTO = new RadnoVremeDTO2();
            radnoVremeDTO.id = radnoVreme.getId();
            radnoVremeDTO.danNedelje = String.valueOf(radnoVreme.getDanNedelje());
            radnoVremeDTO.vremeOtvaranja = radnoVreme.getVremeOtvaranja();
            radnoVremeDTO.vremeZatvaranja = radnoVreme.getVremeZatvaranja();
            radnoVremeDTOi.add(radnoVremeDTO);
        }

        return ResponseEntity.ok(radnoVremeDTOi);
    }

    @PostMapping("/ukloniradnovreme")
    public ResponseEntity<RadnoVremeDTO> ukloniRadnoVreme(@RequestParam(value = "id", required = true) String id,
                                                         @RequestHeader("authorization") String token) {

        try {
            Integer.valueOf(id);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.ADMIN) {
                    radnoVremeRepozitorijum.obrisi(Integer.parseInt(id));

                    return new ResponseEntity<>(null, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    // ==================== ELASTICSEARCH & MINIO ENDPOINTS ====================

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadFiles(
            @PathVariable Integer id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "pdf", required = false) MultipartFile pdf,
            @RequestHeader(value = "authorization", required = false) String token) {

        log.info("Upload request received for centar ID: {}", id);
        log.info("Image present: {}, PDF present: {}", image != null && !image.isEmpty(), pdf != null && !pdf.isEmpty());

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from token", e);
        }

        if (email == null) {
            log.warn("No email found in token");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication required");
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            log.warn("User not found or inactive: {}", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized");
        }

        if (korisnik.getRole() != enumRole.ADMIN) {
            log.warn("User {} is not admin", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        try {
            Centar updatedCentar = centarServis.uploadFiles(id, image, pdf);
            log.info("Files uploaded successfully for centar ID: {}", id);
            return ResponseEntity.ok(updatedCentar);
        } catch (Exception e) {
            log.error("Error uploading files for centar ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading files: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> downloadPdf(
            @PathVariable Integer id,
            @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {
        }

        if (email == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            InputStream pdfStream = centarServis.downloadPdf(id);
            org.springframework.core.io.InputStreamResource resource = 
                    new org.springframework.core.io.InputStreamResource(pdfStream);

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"centar-" + id + ".pdf\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("PDF not found: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<?> getImage(@PathVariable Integer id) {
        try {
            Centar centar = centarRepozitorijum.findById(id);
            if (centar == null || centar.getImagePath() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Image not found for center: " + id);
            }

            InputStream imageStream = centarServis.downloadImage(id);
            org.springframework.core.io.InputStreamResource resource = 
                    new org.springframework.core.io.InputStreamResource(imageStream);

            // Determine content type from file extension
            String imagePath = centar.getImagePath();
            org.springframework.http.MediaType contentType = org.springframework.http.MediaType.IMAGE_JPEG;
            if (imagePath.toLowerCase().endsWith(".png")) {
                contentType = org.springframework.http.MediaType.IMAGE_PNG;
            } else if (imagePath.toLowerCase().endsWith(".gif")) {
                contentType = org.springframework.http.MediaType.IMAGE_GIF;
            }

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found: " + e.getMessage());
        }
    }

    @PostMapping("/reindex")
    public ResponseEntity<?> reindexAllCentri(@RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {
        }

        if (email == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        if (korisnik.getRole() != enumRole.ADMIN) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            centarServis.indexAllCentri();
            return ResponseEntity.ok("All centers reindexed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reindexing: " + e.getMessage());
        }
    }

    @GetMapping("/debug-reviewcount")
    public ResponseEntity<?> debugReviewCount(@RequestHeader("authorization") String token) {
        
        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {
        }

        if (email == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        try {
            // Get all centers from Elasticsearch
            List<CentarDocument> allDocs = centarServis.searchAll("*");
            
            List<Map<String, Object>> debugInfo = new ArrayList<>();
            for (CentarDocument doc : allDocs) {
                Map<String, Object> info = new HashMap<>();
                info.put("id", doc.getId());
                info.put("ime", doc.getIme());
                info.put("reviewCount", doc.getReviewCount());
                
                // Also get actual count from database for comparison
                List<Ocena> reviews = ocenaRepozitorijum.oceneObjekta(doc.getId());
                info.put("actualReviewCount", reviews.size());
                info.put("match", Objects.equals(doc.getReviewCount(), reviews.size()));
                
                debugInfo.add(info);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalCenters", debugInfo.size());
            response.put("centers", debugInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("trace", e.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============ NEW ENDPOINTS FOR ELASTICSEARCH AND MINIO ============

    /**
     * Unified search endpoint - searches across name, description, and PDF content
     * GET /api/centri/search?query=search_text
     */
    @GetMapping("/search")
    public ResponseEntity<List<CentarDTO>> searchCentri(
            @RequestParam("query") String query,
            @RequestHeader("authorization") String token) {

        log.info("========== SEARCH ENDPOINT CALLED ==========");
        log.info("Query parameter: {}", query);

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {
        }

        if (email == null) {
            log.warn("Token invalid or expired - returning 403");
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);
        if (korisnik == null || !korisnik.isActive()) {
            log.warn("User not found or inactive - returning 403");
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        log.info("User authenticated: {}", email);

        try {
            log.info("Calling centarServis.searchAll() with query: {}", query);
            // Search across all fields using Elasticsearch
            List<CentarDocument> searchResults = centarServis.searchAll(query);
            log.info("Search returned {} results", searchResults.size());

            // Convert to DTOs
            List<CentarDTO> centarDTOS = new ArrayList<>();
            for (CentarDocument doc : searchResults) {
                CentarDTO dto = new CentarDTO();
                dto.setId(doc.getId());
                dto.setIme(doc.getIme());
                dto.setOphis(doc.getOpis());
                dto.setDatumKreacije(doc.getDatumKreacije());
                dto.setAdresa(doc.getAdresa());
                dto.setGrad(doc.getGrad());
                dto.setRating(doc.getRating());
                
                // Set image URL instead of path
                if (doc.getImagePath() != null && !doc.getImagePath().isEmpty()) {
                    dto.setImagePath("/api/centri/" + doc.getId() + "/image");
                }
                
                // Set PDF URL instead of path
                if (doc.getPdfPath() != null && !doc.getPdfPath().isEmpty()) {
                    dto.setPdfPath("/api/centri/" + doc.getId() + "/pdf");
                    dto.setHasPdf(true);
                } else {
                    dto.setHasPdf(false);
                }
                
                centarDTOS.add(dto);
            }

            return ResponseEntity.ok(centarDTOS);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

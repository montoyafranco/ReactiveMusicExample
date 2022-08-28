package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.ISongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {
    @Mock
    ISongRepository iSongRepositoryMock;

    ModelMapper modelMapper ;// Helper

    SongServiceImpl songService;

    @BeforeEach
    void init(){
        modelMapper = new ModelMapper();
        songService = new SongServiceImpl(iSongRepositoryMock,modelMapper);
    }



    @Test
    @DisplayName("findAllSongs()")
    void findAllSongs() {
        //focus on status code
        //1 armar el escenario con la respuesta esperada
        ///------------------------------armar el escenario con la respuesta esperada-------------------

        //Creo el array de canciones y le agrego 2 cancions con add
        ArrayList<Song> listSongs = new ArrayList<>();
        listSongs.add(new Song());
        listSongs.add(new Song());

        //Creo un arraylist nuevo con song tranformado a SongDTO dandole transformacion
        // con el maper del array creado arriba como funcion BeforeEach

        ArrayList<SongDTO> listSongsDTO = listSongs.stream()
                .map(song -> modelMapper.map(song,SongDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));

        var fluxResult = Flux.fromIterable(listSongs);
        var fluxResultDTO =Flux.fromIterable(listSongsDTO);

        //2 Lo que voy a esperar de respuesta
        //------------------------------Lo que voy a esperar de respuesta-------------------
        // no lo uso luego pero sirve para saber q deberia esperar
        ResponseEntity<Flux<SongDTO>> respEntResult =new ResponseEntity<>(fluxResultDTO,HttpStatus.FOUND);


        //3 . Mockeo el resultado q espero
        //------------------------------Mockeo el resultado q espero-------------------
        //espero que me retorne del repositorio un array list de songs en la que le aplico Flux.fromIterable
        Mockito    .when(iSongRepositoryMock   .findAll()) .thenReturn(fluxResult);

        //4. Servicio
        //llamo al servicio para realmente testearlo ya
        var service = songService.findAllSongs();

        //5.Step Verifier
        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity -> fluxResponseEntity.getStatusCode().is3xxRedirection())
                .expectComplete()
                .verify();







    }
    @Test
    @DisplayName("findAllSongError()")
    void findAllSongsError() {
        // code 2xx necesito para que ande
        Mockito.when(iSongRepositoryMock.findAll()).thenReturn(Flux.empty());

        var service = songService.findAllSongs();

        //Step verifier,
        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity -> fluxResponseEntity.getStatusCode().is2xxSuccessful())
                .expectComplete().verify();


    }

    @Test
    @DisplayName("findSongById()")
    void findSongById() {
        //1 armar el escenario con la respuesta esperada
        Song songExpected = new Song("1",
                "agus",
                "2",
                "afg",
                "mozart",
                "duki", LocalTime.of(0,0,0));

        //mapeo el song creado a songDTO
        var songDTOExpected = modelMapper.map(songExpected,SongDTO.class);

        //lo que deberia esperar correctamente
        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.FOUND);

        ////3 . Mockeo el resultado q espero
        //espero un mono de song q me traiga el repository
        Mockito.when(iSongRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));

        //4 - invoco el servicio para probarlo
        var service = songService.findSongById("1");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();





    }
    @Test
    @DisplayName("findSongByIdError()")
    void findSongByIdError() {
        //lo que deberia esperar correctamente
        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        ////3 . Mockeo el resultado q espero
        //espero un mono de song q me traiga el repository empty
        Mockito.when(iSongRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        //4 - invoco el servicio para probarlo
        var service = songService.findSongById("2");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();


        //compruebo q utilice mis parametros
        Mockito.verify(iSongRepositoryMock).findById("2");

    }

    @Test
    void saveSong() {
        Song songExpected = new Song("1",
                "agus",
                "2",
                "afg",
                "mozart",
                "duki", LocalTime.of(0,0,0));
        var songDTOExpected = modelMapper.map(songExpected, SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.CREATED);

        Mockito.when(iSongRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.just(songExpected));

        var service = songService.saveSong(songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(iSongRepositoryMock).save(songExpected);
    }
    @Test
    void saveSongError() {
        Song songExpected = new Song("1",
                "agus",
                "2",
                "afg",
                "mozart",
                "duki", LocalTime.of(0,0,0));
        var songDTOExpected = modelMapper.map(songExpected, SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        Mockito.when(iSongRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.empty());

        var service = songService.saveSong(songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(iSongRepositoryMock).save(Mockito.any(Song.class));
    }

    @Test
    void updateSong() {
        Song expectedSong = new Song(
                "1",
                "agus",
                "2",
                "afg",
                "mozart",
                "duki", LocalTime.of(0,0,0)
        );

        var editedSong = expectedSong.toBuilder()
                .name("actualizado").build();
        var songEditedDTO = modelMapper.map(editedSong, SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponseEntity = new ResponseEntity<>(songEditedDTO, HttpStatus.ACCEPTED);

        Mockito.when(iSongRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(expectedSong));
        Mockito.when(iSongRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.just(editedSong));

        var service = songService.updateSong("1", songEditedDTO);

        StepVerifier.create(service)
                .expectNext(songDTOResponseEntity)
                .expectComplete().verify();


    }

    @Test
    void deleteSong() {
        //1
        Song expectedSong = new Song(
                "1",
                "agus",
                "2",
                "afg",
                "mozart",
                "duki", LocalTime.of(0,0,0)
        );
        //me pide String el service como respuesta
        ResponseEntity<String> songDTOResponseEntity = new ResponseEntity<>(expectedSong.getIdSong(), HttpStatus.ACCEPTED);
        //encuentro el id
        Mockito.when(iSongRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(expectedSong));
        //lo borro y retorno mono empty
        Mockito.when(iSongRepositoryMock.deleteById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service =songService.deleteSong("1");
        StepVerifier.create(service).expectNext(songDTOResponseEntity).expectComplete().verify();



    }
    @Test
    void deleteSongError() {
        //1

//        Song expectedSong = new Song(
//                "1",
//                "agus",
//                "2",
//                "afg",
//                "mozart",
//                "duki", LocalTime.of(0,0,0)
//        );
        //me pide String el service como respuesta
        ResponseEntity<String> songDTOResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Mockito.when(iSongRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());


        var service =songService.deleteSong("2");

        StepVerifier.create(service).expectNext(songDTOResponseEntity).expectComplete().verify();

        Mockito.verify(iSongRepositoryMock).findById(Mockito.any(String.class));



    }
    @Test
    void updateSongError() {
        Song expectedSong = new Song(
                "1",
                "agus",
                "2",
                "afg",
                "mozart",
                "duki", LocalTime.of(0,0,0)
        );
        var editSong = expectedSong.toBuilder().name("prueba").build();

        var songEditada = modelMapper.map(editSong,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponseEntity = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

        Mockito.when(iSongRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.updateSong("1",songEditada);

        StepVerifier.create(service).expectNext(songDTOResponseEntity)
                .expectComplete().verify();

        Mockito.verify(iSongRepositoryMock).findById(Mockito.any(String.class));


    }

}
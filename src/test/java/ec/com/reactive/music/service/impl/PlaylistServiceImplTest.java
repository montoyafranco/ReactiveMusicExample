package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.IPlaylistRepository;
import ec.com.reactive.music.repository.ISongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class PlaylistServiceImplTest {

    @InjectMocks
    private PlaylistServiceImpl playlistService;
    @Mock
    private ISongRepository iSongRepository;
    @Mock
    private IPlaylistRepository iPlaylistRepository;
    @Spy
    private ModelMapper modelMapper;
    // espia los metodos , instacia de verdad y despues le puedes aplicar mockito.when etc (obliga que este)
    //Junit no analiza toda la aplicacion y Modelmapper lo instacia a la fuerza a q exista con Spy sino null pointer

    @Test
    void testDeletePlaylist() {

        Mockito
                .when(iPlaylistRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Mono.empty());
        //act

        Mono<ResponseEntity<String>> response = this.playlistService.deletePlaylist("ayudaaa");

        //
        //assert

        StepVerifier.create(response)
                .expectNextMatches(stringResponseEntity -> stringResponseEntity.getStatusCode().is4xxClientError())
                .verifyComplete();

    }

    //addSongPlaylistError
    @Test
    void addSongPlaylist() {
        //Argument matcher cuando reciba parametro no importa el valor del parametro
        //  va a ser match y mockeara ese motodo (Concepto de testin de espiar metodo)
        Mockito.when(iSongRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(new Song("agustin", "montoya", "sdasdas", "aprendo", "test", "Mozart"
                        , LocalTime.of(0, 5, 6))));

        Playlist playlist1 = new Playlist("asdas", "asdasd", "asdasd", new ArrayList<>(), LocalTime.of(0, 0, 0));

        Mockito.when(iPlaylistRepository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Mono.just(playlist1));

        Mockito.when(iPlaylistRepository.save(ArgumentMatchers.any(Playlist.class)))
                .thenReturn(Mono.just(playlist1));

        //act
        Mono<ResponseEntity<PlaylistDTO>> responseEntityMono = this.playlistService.addSongPlaylist(playlist1.getIdPlaylist(), "sadasd");

        //assert

        StepVerifier.create(responseEntityMono)
                .expectSubscription()
                .expectNextMatches(responseEntity ->
                        responseEntity.getBody().getSongs().size() == 1 && responseEntity.getStatusCode().is2xxSuccessful())
                .verifyComplete();

    }


}

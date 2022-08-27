package ec.com.reactive.music.service;


import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlaylistService {

        Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists();




        Mono<ResponseEntity<PlaylistDTO>> savePlaylist (PlaylistDTO playlistDTO);
        Mono<ResponseEntity<PlaylistDTO>> addSongPlaylist (String idPlaylist, String idSong);

        Mono<ResponseEntity<PlaylistDTO>> updatePlaylist (String idPlaylist, PlaylistDTO playlistDTO);

        Mono<ResponseEntity<PlaylistDTO>> removeSongPlaylist(String idPlaylist, String idSong);




        Mono<ResponseEntity<PlaylistDTO>> findPlaylistById(String id);
        Mono<ResponseEntity<String>> deletePlaylist (String idPlaylist);


        PlaylistDTO entityToDTO(Playlist playlist);
        Playlist dtoToEntity(PlaylistDTO playlistDTO);
    }


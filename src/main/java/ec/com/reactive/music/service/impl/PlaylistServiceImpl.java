package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.IPlaylistRepository;
import ec.com.reactive.music.repository.ISongRepository;
import ec.com.reactive.music.service.IPlaylistService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistServiceImpl implements IPlaylistService {

    @Autowired
    private IPlaylistRepository iPlaylistRepository;

    @Autowired
    private ISongRepository iSongRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists() {
        return this.iPlaylistRepository
                .findAll()
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NO_CONTENT.toString())))
                .map(this::entityToDTO)
                .collectList()
                .map(playlistDTOS -> new ResponseEntity<>(Flux.fromIterable(playlistDTOS), HttpStatus.FOUND))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(Flux.empty(), HttpStatus.NO_CONTENT)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> findPlaylistById(String id) {

        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO, HttpStatus.FOUND))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> savePlaylist(PlaylistDTO playlistDTO) {

        return this.iPlaylistRepository
                .save(dtoToEntity(playlistDTO))
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.EXPECTATION_FAILED.toString())))
                .map(this::entityToDTO)
                .map(playlistDTO1 -> new ResponseEntity<>(playlistDTO1, HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> addSongPlaylist(String idPlaylist, String idSong) {
        return this.iPlaylistRepository.findById(idPlaylist)
                .flatMap(playlist -> {
                    return this.iSongRepository.findById(idSong).doOnNext(
                            song -> {
                                ArrayList<Song> songs = playlist.getSongs();
                                songs.add(song);
                                playlist.setSongs(songs);
                            }
                    ).thenReturn(playlist);
                }).map(playlist -> {
                    ArrayList<Song> songs = playlist.getSongs();
                   LocalTime duration = songs.stream()
                            .map(Song::getDuration)
                            .reduce(LocalTime.of(0,0,0),(ac ,songDuration  ) ->{
                                ac = ac.plusHours(songDuration.getHour())
                                        .plusMinutes(songDuration.getMinute())
                                        .plusSeconds(songDuration.getSecond());
                                return ac ;
                            } );
                    playlist.setDuration(duration);
                    return playlist;
                }).flatMap(this.iPlaylistRepository::save)
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO,HttpStatus.OK));




    }
    public Mono<ResponseEntity<PlaylistDTO>> removeSongPlaylist(String idPlaylist, String idSong) {
        return this.iPlaylistRepository.findById(idPlaylist)
                .flatMap(playlist -> {
                    return this.iSongRepository.findById(idSong).doOnNext(
                            song -> {
                                ArrayList<Song> songs = (ArrayList<Song>) playlist.getSongs()
                                        .stream().filter(song1 -> !song1.getIdSong().equals(song.getIdSong()))
                                        .collect(Collectors.toList());
                                playlist.setSongs(songs);
                            }
                    ).thenReturn(playlist);
                }).map(playlist -> {
                    ArrayList<Song> songs = playlist.getSongs();
                    LocalTime duration = songs.stream()
                            .map(Song::getDuration)
                            .reduce(LocalTime.of(0,0,0),(ac ,songDuration  ) ->{
                                ac = ac.plusHours(songDuration.getHour())
                                        .plusMinutes(songDuration.getMinute())
                                        .plusSeconds(songDuration.getSecond());
                                return ac ;
                            } );
                    playlist.setDuration(duration);
                    return playlist;
                }).flatMap(this.iPlaylistRepository::save)
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO,HttpStatus.OK));




    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> updatePlaylist(String idPlaylist, PlaylistDTO playlistDTO) {

        return this.iPlaylistRepository
                .findById(idPlaylist)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    playlistDTO.setIdPlaylist(playlist.getIdPlaylist());
                    return this.savePlaylist(playlistDTO);
                })
                .map(playlistDTOResponseEntity -> new ResponseEntity<>(playlistDTOResponseEntity.getBody(), HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }

    @Override
    public Mono<ResponseEntity<String>> deletePlaylist(String idPlaylist) {

        return this.iPlaylistRepository
                .findById(idPlaylist)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> this.iPlaylistRepository
                        .deleteById(playlist.getIdPlaylist())
                        .map(monoVoid -> new ResponseEntity<>(idPlaylist, HttpStatus.ACCEPTED)))
                .thenReturn(new ResponseEntity<>(idPlaylist, HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public PlaylistDTO entityToDTO(Playlist playlist) {
        return this.modelMapper.map(playlist, PlaylistDTO.class);
    }

    @Override
    public Playlist dtoToEntity(PlaylistDTO playlistDTO) {
        return this.modelMapper.map(playlistDTO, Playlist.class);
    }
}
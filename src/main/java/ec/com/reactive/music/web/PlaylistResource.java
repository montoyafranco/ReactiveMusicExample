package ec.com.reactive.music.web;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.service.IPlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class PlaylistResource {

    @Autowired
    private IPlaylistService playlistService;


    @DeleteMapping("/deletePlaylist/{id}")
    private Mono<ResponseEntity<String>> deletePlaylist(@PathVariable String id){
        return playlistService.deletePlaylist(id);
    }
    @GetMapping("/findAllPlaylists")
    private Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists(){
        return playlistService.findAllPlaylists();
    }
    @PutMapping("/{idPlaylist}/addSongPlay/{idSong}")
    public Mono<ResponseEntity<PlaylistDTO>> addSongPlaylist(@PathVariable("idPlaylist") String idPlaylist,@PathVariable("idSong") String idSong) {
        return playlistService.addSongPlaylist(idPlaylist,idSong);

    }
    @PutMapping("/{idPlaylist}/removeSongPlay/{idSong}")
    public Mono<ResponseEntity<PlaylistDTO>> removeSongPlaylist(@PathVariable("idPlaylist") String idPlaylist,@PathVariable("idSong") String idSong) {
        return playlistService.removeSongPlaylist(idPlaylist,idSong);

    }

    @GetMapping("/findPlaylist/{id}")
    private Mono<ResponseEntity<PlaylistDTO>> findPlaylistById(@PathVariable String id){
        return playlistService.findPlaylistById(id);
    }



    @PutMapping("/updatePlaylist/{id}")
    private  Mono<ResponseEntity<PlaylistDTO>> updatePlaylist(@PathVariable String id,
                                                              @RequestBody PlaylistDTO playlistDTO){
        return playlistService.updatePlaylist(id,playlistDTO);
    }
    @PostMapping("/savePlaylist")
    private Mono<ResponseEntity<PlaylistDTO>> savePlaylist(@RequestBody PlaylistDTO playlistDTO){
        return playlistService.savePlaylist(playlistDTO);
    }


}

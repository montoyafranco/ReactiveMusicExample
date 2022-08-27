package ec.com.reactive.music.repository;

import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Playlist;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IPlaylistRepository extends ReactiveMongoRepository<Playlist,String> {
}

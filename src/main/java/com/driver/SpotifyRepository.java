package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User u=new User(name,mobile);
        users.add(u);
        return u;
    }

    public Artist createArtist(String name) {
        Artist a=new Artist(name);
        artists.add(a);
        return  a;
    }

    public Album createAlbum(String title, String artistName) {
        int n=artists.size();
        int f=0;
        Artist artist1 = null;
        for(int i=0;i<n;i++)
        {
            if(artists.get(i).getName().equals(artistName))
            { f=1; artist1 =artists.get(i);}


        }
        if(f==0)//creat artist
        {
            Artist a=new Artist(artistName);
            artists.add(a);//......


            Album ab=new Album(artistName);
            albums.add(ab);
            List<Album> lt = new ArrayList<>();
            lt.add(ab);
            artistAlbumMap.put(a,lt);

            return ab;
        }
         else {
            Album ab = new Album(title);

            albums.add(ab);

            List<Album> l = artistAlbumMap.get(artist1);
            if(l == null){
                l = new ArrayList<>();
            }
            l.add(ab);
            artistAlbumMap.put(artist1,l);

            return ab;
        }

    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album ab = null;
        for(Album A:albums){
            if(A.getTitle()==albumName){
                ab=A;
                break;
            }
        }
        if(ab==null)
            throw new Exception("Album does not exist");
        else {
            Song song = new Song(title,length);
            songs.add(song);
            if(albumSongMap.containsKey(ab)){
                List<Song> lt = albumSongMap.get(ab);
                lt.add(song);
                albumSongMap.put(ab,lt);
            }else{
                List<Song> sList = new ArrayList<>();
                sList.add(song);
                albumSongMap.put(ab,sList);
            }

            return song;
        }


    }



    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User us:users){
            if(us.getMobile()==mobile){
                user=us;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist pl = new Playlist(title);

            playlists.add(pl);

            List<Song> lt = new ArrayList<>();
            for(Song song:songs){
                if(song.getLength()==length){
                    lt.add(song);
                }
            }
            playlistSongMap.put(pl,lt);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(pl,list);

            creatorPlaylistMap.put(user,pl);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> user_List = userPlaylistMap.get(user);
                user_List.add(pl);
                userPlaylistMap.put(user,user_List);
            }else{
                List<Playlist> p = new ArrayList<>();
                p.add(pl);
                userPlaylistMap.put(user,p);
            }

            return pl;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        User user = null;
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist pl = new Playlist(title);

            playlists.add(pl);

            List<Song> lt = new ArrayList<>();
            for (Song song : songs) {
                if (songTitles.contains(song.getTitle())) {
                    lt.add(song);
                }
            }
            playlistSongMap.put(pl, lt);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(pl, list);

            creatorPlaylistMap.put(user, pl);

            if (userPlaylistMap.containsKey(user)) {
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(pl);
                userPlaylistMap.put(user, userPlayList);
            } else {
                List<Playlist> plays = new ArrayList<>();
                plays.add(pl);
                userPlaylistMap.put(user, plays);
            }

            return pl;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;

        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }

        if(user==null)
            throw new Exception("User does not exist");

        Playlist pl = null;
        for(Playlist playlist1:playlists){
            if(playlist1.getTitle()==playlistTitle){
                pl=playlist1;
                break;
            }
        }
        if(pl==null)
            throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user))
            return pl;

        List<User>  userlist2 = playlistListenerMap.get(pl);
        for(User user1:userlist2){
            if(user1==user)
                return pl;
        }

        userlist2.add(user);
        playlistListenerMap.put(pl,userlist2);

        List<Playlist> playlists1 = userPlaylistMap.get(user);
        if(playlists1 == null){
            playlists1 = new ArrayList<>();
        }
        playlists1.add(pl);
        userPlaylistMap.put(user,playlists1);

        return pl;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User u:users){
            if(u.getMobile()==mobile){
                user=u;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        Song song = null;
        for(Song s:songs){
            if(s.getTitle()==songTitle){
                song=s;
                break;
            }
        }
        if (song==null)
            throw new Exception("Song does not exist");

        if(songLikeMap.containsKey(song)){
            List<User> lt = songLikeMap.get(song);
            if(lt.contains(user)){
                return song;
            }else {

                song.setLikes(song.getLikes() + 1);
                lt.add(user);
                songLikeMap.put(song,lt);

                Album ab=null;
                for(Album album1:albumSongMap.keySet()){
                    List<Song> song_List = albumSongMap.get(album1);
                    if(song_List.contains(song)){
                        ab = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist a:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(a);
                    if (albumList.contains(ab)){
                        artist = a;
                        break;
                    }
                }

                artist.setLikes(artist.getLikes() +1);
                artists.add(artist);
                return song;
            }
        }else {

            song.setLikes(song.getLikes() + 1);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album a:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(a);
                if(songList.contains(song)){
                    album = a;
                    break;
                }
            }
            Artist artist = null;
            for(Artist art:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(art);
                if (albumList.contains(album)){
                    artist = art;
                    break;
                }
            }
            int likes1 = artist.getLikes() +1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int n=artists.size();
        String ans="";
        int max=0;
        for(int i=0;i<n;i++)
        {
         int p=artists.get(i).getLikes();
         if(p>max)
         {
             max=p;
             ans=artists.get(i).getName();
         }
        }
        return  ans;
    }

    public String mostPopularSong() {
        int n=songs.size();
        String ans="";
        int max=0;
        for(int i=0;i<n;i++)
        {
            int p=songs.get(i).getLikes();
            if(p>max)
            {
                max=p;
                ans=songs.get(i).getTitle();
            }
        }
        return  ans;
    }
}

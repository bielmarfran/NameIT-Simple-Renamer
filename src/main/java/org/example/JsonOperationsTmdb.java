package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JsonOperationsTmdb {
	
	//Try to connect to the Api with the stored Api key.
	public static void checkConnection(){
		System.out.println("checkConnectionTMDB");
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		uri ="https://api.themoviedb.org/3/movie/76341?api_key="+keynow;
			

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::statusCode)
		.thenApply(MainController::statusTMDB)
		.join();

	}

	//Send info to TMDB API to get a Response from Movies
	public static void getSearchMovie(String name, int year){
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		if(year==0) {
			
			uri ="https://api.themoviedb.org/3/search/movie?api_key="+keynow+"&language="+language+"&query="+name+
					"&page=1&include_adult=false";
			System.out.println(uri);
		}else {
			uri ="https://api.themoviedb.org/3/search/movie?api_key="+keynow+"&language="+language+"&query="+name+
					"&page=1&include_adult=false&primary_release_year="+year;
			System.out.println(uri);
		}
	

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				//.header("Accept-Language", language)
				//.header("Authorization", "Bearer "+keynow)
			
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::body)
		.thenApply(OperationTmdb::responseMovieId)
		.join();

	}
	//Send info to TMDB API to get a Response from Series
	public static void getSearchSerie(String name, int year){
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		if(year==0) {				
			uri ="https://api.themoviedb.org/3/search/tv?api_key="+keynow+"&language="+language+"&query="+name+
					"&page=1&include_adult=false";
			System.out.println(uri);
		}else {
			uri ="https://api.themoviedb.org/3/search/tv?api_key="+keynow+"&language="+language+"&query="+name+
					"&page=1&include_adult=false&first_air_date_year="+year;
			System.out.println(uri);
		}
	

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				//.header("Accept-Language", language)
				//.header("Authorization", "Bearer "+keynow)
			
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::body)
		.thenApply(OperationTmdb::responseSerieId)
		.join();

	}
	//Get Info for a specific episode
	public static void getInfoSerie(Integer id,String season,String episode){
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";	
			
		uri ="https://api.themoviedb.org/3/tv/"+id+"/season/"+season+"/episode/"+episode+"?api_key="+keynow+"&language="+language;
		//https://api.themoviedb.org/3/tv/60735/season/01/episode/01?api_key=ee7c5286c8b982e91fafcbbcce8ceb30&language=en-US
		System.out.println(uri);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				//.header("Accept-Language", language)
				//.header("Authorization", "Bearer "+keynow)
			
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::body)
		.thenApply(OperationTmdb::responseFinalSerie)
		.join();

	}
	//	
	public static String languageTmdb(String l) {
		switch (l) {
		case "en": 
			l ="en-US";				
			break;
		//case "pt-br": 
			//language ="PT-BR - Português Brasileiro";				
			//break;
		case "de": 
			l ="de";
			break;
		case "pt": 
			l ="pt-PT";
			break;
		case "es": 
			l ="es-ES";
			break;
		case "fr": 
			l="fr-FR";		
			break;
		default:
			break;
		}
	
		return l;
		
	}
	//Get the EpisodeGroups for a given Series
	public static void getSerieEpisodeGroups(Integer id){
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";

		uri ="https://api.themoviedb.org/3/tv/"+id+"/episode_groups?api_key="+keynow+"&language="+language;
		System.out.println(uri);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				//.header("Accept-Language", language)
				//.header("Authorization", "Bearer "+keynow)
			
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::body)
		.thenApply(OperationTmdb::responseSerieEpisodeGroups)
		.join();

	}
	//Get the info from a given EpisodeGroups
	public static void getContentEpisodeGroups(String id){
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		uri ="https://api.themoviedb.org/3/tv/episode_group/"+id+"?api_key="+keynow+"&language="+language;
		System.out.println(uri);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				//.header("Accept-Language", language)
				//.header("Authorization", "Bearer "+keynow)
			
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::body)
		.thenApply(OperationTmdb::responseContentEpisodeGroups)
		.join();

	}
	
	public static void getSeriesKeywords(Integer id){
		String keynow = "ee7c5286c8b982e91fafcbbcce8ceb30";
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		uri ="https://api.themoviedb.org/3/tv/"+id+"/keywords?api_key="+keynow;
		//https://api.themoviedb.org/3/tv/37854/keywords?api_key=ee7c5286c8b982e91fafcbbcce8ceb30
		System.out.println(uri);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))				
				.header("Content-Type", "application/json")
				//.header("Accept-Language", language)
				//.header("Authorization", "Bearer "+keynow)
			
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::body)
		.thenApply(OperationTmdb::checkAnime)
		.join();

	}
	//Get ID Groups
	//Object 2 = Absolute Episode Numbers
	//

}

/*
 *  Copyright (C) 2020-2021 Gabriel Martins Franzin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.bielmarfran.nameit;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.github.bielmarfran.nameit.controllers.MainController;
import com.github.bielmarfran.nameit.dao.DataStored;
import com.github.bielmarfran.nameit.dao.DatabaseOperationsTmdb;
import com.github.bielmarfran.nameit.dao.QueryInfo;

/**
 * This class  stores the methods that deal with https request methods for the TMDB API
 * 
 * @author bielm
 *
 */

public class JsonOperationsTmdb {
	

	/**
	 * This method verifies the connection with the TMDB API, for the moment I have not found a
	 * special request to check the status of the API, so in this method I use a  generic get
	 * request that is presented in the official API guide.
	 */
	public static void checkConnection(){

		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";	
		//uri ="https://api.themoviedb.org/3/movie/76341";
		uri ="https://api.themoviedb.org/3/movie/76341?api_key="+keynow;


		
		  final HttpClient client = HttpClient.newBuilder()
		            .version(HttpClient.Version.HTTP_2)
		            .connectTimeout(Duration.ofSeconds(10))
		            .build();
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(uri))		
				//.header("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlZTdjNTI4NmM4Yjk4M"
					//	+ "mU5MWZhZmNiYmNjZThjZWIzMCIsInN1YiI6IjVlZmU0NmVhOWRlZTU4MDAzNWNmYmZhMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.CXNPdD5fMeOFvL5wJT4vhsmkMKbOA2SpndOlmv0EVOY")
				.header("Content-Type", "application/json")
				.build();

	
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
		.thenApply(HttpResponse::statusCode)
		.thenApply(MainController::statusTMDB)
		.join();  
		 

		 



		
		
	}


	/**
	 * This method is called to make an API request, to find information about Movies.
	 * 
	 * @param name Movie Name
	 * @param year Year the film was released
	 */
	public static void getSearchMovie(String query, int year){
		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		String nameEncoded =URLEncoder.encode(query, StandardCharsets.UTF_8);
		nameEncoded = nameEncoded.replace("+", "%20");
		if(year==0) {

			uri ="https://api.themoviedb.org/3/search/movie?api_key="+keynow+"&language="+language+"&query="+nameEncoded+
					"&page=1&include_adult=false";
		}else {
			uri ="https://api.themoviedb.org/3/search/movie?api_key="+keynow+"&language="+language+"&query="+nameEncoded+
					"&page=1&include_adult=false&primary_release_year="+year;

		}


		QueryInfo queryInfo = new QueryInfo();
		setQuery(queryInfo, nameEncoded, language, year, "Movies");

		
		if(!DatabaseOperationsTmdb.selectMovie(queryInfo)) {

			try {
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(uri))				
						.header("Content-Type", "application/json")
						//.header("Accept-Language", language)
						//.header("Authorization", "Bearer "+keynow)

						.build();

				client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenApply(OperationTmdbMovie::responseMovieId)
				.join();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//System.out.println(e);		
			}			
		}	
	}

	
	/**
	 * This method is called to make an API request, to find information about Series.
	 * 
	 * @param name Series Name
	 * @param year Year the series has started
	 */
	public static void getSearchSerie(String query, int year){
		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		String nameEncoded =URLEncoder.encode(query, StandardCharsets.UTF_8);
		nameEncoded = nameEncoded.replace("+", "%20");
		if(year==0) {				
			uri ="https://api.themoviedb.org/3/search/tv?api_key="+keynow+"&language="+language+"&query="+nameEncoded+
					"&page=1&include_adult=false";
	
		}else {
			uri ="https://api.themoviedb.org/3/search/tv?api_key="+keynow+"&language="+language+"&query="+nameEncoded+
					"&page=1&include_adult=false&first_air_date_year="+year;

		}

		QueryInfo queryInfo= new QueryInfo();
		setQuery(queryInfo, nameEncoded, language, year, "Series");
		
		if(!DatabaseOperationsTmdb.selectSerieInformation(queryInfo,"SeriesQueries")) {
			
			try {
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(uri))				
						.header("Content-Type", "application/json")
						//.header("Accept-Language", language)
						//.header("Authorization", "Bearer "+keynow)
					
						.build();

				client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenApply(OperationTmdbSerie::responseSerieId)
				.join();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//System.out.println(e);
			}
		}
		

	}
	
	

	/**
	 * This method is called to make an API request, to find information about specific episode
	 * 
	 * @param id Id of the series
	 * @param season Season value
	 * @param episode Episode value
	 */
	public static void getInfoSerie(Integer id,String season,String episode){
		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";	
			
		uri ="https://api.themoviedb.org/3/tv/"+id+"/season/"+season+"/episode/"+episode+"?api_key="+keynow+"&language="+language;


		QueryInfo queryInfo= new QueryInfo();
		setQuery(queryInfo, id+"/season/"+season+"/episode/"+episode, language, 0, "Series");
		
		if(!DatabaseOperationsTmdb.selectSerieInformation(queryInfo,"SeriesQueriesInfo")) {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(uri))				
					.header("Content-Type", "application/json")
					//.header("Accept-Language", language)
					//.header("Authorization", "Bearer "+keynow)
				
					.build();

			client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(OperationTmdbSerie::responseFinalSerie)
			.join();
		}


	}

	

	/**
	 *This method is called to make an API request, returning the groups of alternative episodes in the series.
	 * 
	 * @param id Id of the series
	 */
	public static void getSerieEpisodeGroups(Integer id){
		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";

		uri ="https://api.themoviedb.org/3/tv/"+id+"/episode_groups?api_key="+keynow+"&language="+language;
		

		QueryInfo queryInfo= new QueryInfo();
		setQuery(queryInfo, id.toString(), language, 0, "Series");
		if(!DatabaseOperationsTmdb.selectSerieInformation(queryInfo,"SeriesEpisodeGroups")) {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(uri))				
					.header("Content-Type", "application/json")
					//.header("Accept-Language", language)
					//.header("Authorization", "Bearer "+keynow)
				
					.build();

			client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(OperationTmdbSerie::responseSerieEpisodeGroups)
			.join();

		}
		
		
	
	}
	

	/**
	 * This method is called to make an API request, returning the alternate episode group values.
	 * 
	 * @param id Id of the episode group
	 */
	public static void getContentEpisodeGroups(String id){
		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		uri ="https://api.themoviedb.org/3/tv/episode_group/"+id+"?api_key="+keynow+"&language="+language;
		

		QueryInfo queryInfo= new QueryInfo();
		setQuery(queryInfo, id, language, 0, "Series");
		
		if(!DatabaseOperationsTmdb.selectSerieInformation(queryInfo,"SeriesContentEpisodeGroups")) {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(uri))				
					.header("Content-Type", "application/json")
					//.header("Accept-Language", language)
					//.header("Authorization", "Bearer "+keynow)
				
					.build();

			client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(OperationTmdbSerie::responseContentEpisodeGroups)
			.join();

			
		}
		
		
	}
	
	
	/**
	 * This method is called to make an API request, returning the keywords of the series
	 * 
	 * @param id Id of the series
	 */
	public static void getSeriesKeywords(Integer id){
		String keynow = DataStored.propertiesGetKey();
		String language = DataStored.propertiesGetLanguage();
		language = languageTmdb(language);
		String uri = "";
		uri ="https://api.themoviedb.org/3/tv/"+id+"/keywords?api_key="+keynow;
		//https://api.themoviedb.org/3/tv/37854/keywords?api_key=ee7c5286c8b982e91fafcbbcce8ceb30
		

		QueryInfo queryInfo= new QueryInfo();
		setQuery(queryInfo, id.toString(), language, 0, "Series");
		
		if(!DatabaseOperationsTmdb.selectSerieInformation(queryInfo,"SeriesKeywords")) {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(uri))				
					.header("Content-Type", "application/json")
					//.header("Accept-Language", language)
					//.header("Authorization", "Bearer "+keynow)
				
					.build();

			client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenApply(OperationTmdbSerie::checkAnime)
			.join();
		}
		
		

	}


	/**
	 * This method convert the stored language information 
	 * to the format that the API recognizes
	 * 
	 * @param l Store language information
	 * @return Language value that API recognizes
	 */
	public static String languageTmdb(String l) {
		String language="";
		switch (l) {
		case "en": 
			language = "en-US";				
			break;
		//case "pt-br": 
			//language ="PT-BR - Português Brasileiro";				
			//break;
		case "de": 
			language = "de";
			break;
		case "pt": 
			language = "pt-PT";
			break;
		case "es": 
			language = "es-ES";
			break;
		case "fr": 
			language = "fr-FR";		
			break;
		default:
			break;
		}
	
		return language;
		
	}

	/**
	 * This method assign values to an queryInfo object, and passes the 
	 * information to a global queryInfo object in the {@link com.github.bielmarfran.nameit.OperationTmdbMovie}
	 * or {@link com.github.bielmarfran.nameit.OperationTmdbSerie} where is needed.
	 * 
	 * @param queryInfo Object with the information worked in the database
	 * @param query Query value
	 * @param language Language identification
	 * @param year Year value
	 * @param mode Current Mode
	 */
	public static void setQuery(QueryInfo queryInfo ,String query, String language, int year, String mode) {

		queryInfo.setQueryValue(query);
		queryInfo.setLanguage(language);
		queryInfo.setYear(year);
		if(mode.equals("Movies")) {
			OperationTmdbMovie.setQueryInfo(queryInfo);
		}else {
			OperationTmdbSerie.setQueryInfo(queryInfo);
		}
		
	}


}

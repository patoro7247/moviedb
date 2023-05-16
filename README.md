Video: https://youtu.be/RnuglOfBoD8

# Prepared Statement Usage:
    
CheckoutServlet.java:

    String query = "select id, firstName, lastName, expiration from creditcards where id=?";
    PreparedStatement statement = conn.prepareStatement(query);

    String movieIdQuery = "select id from movies where title=?";
    PreparedStatement movieIdStatement = conn.prepareStatement(movieIdQuery);

    String insertSalesQuery = "INSERT INTO sales VALUES (NULL, ?, ?, DATE ?)";
    PreparedStatement salesInsertStatement = conn.prepareStatement(insertSalesQuery);


LoginServlet.java:
           
    String query = "SELECT password, id from customers where email=?";
    PreparedStatement statement = conn.prepareStatement(query);

            
MoviesServlet.java:

    String genreQuery = "SELECT g.name FROM genres as g, genres_in_movies as gim, movies as m WHERE g.id=gim.genreId AND m.id=? AND m.id=gim.movieId ORDER BY g.name ASC LIMIT 3";
    PreparedStatement statement2 = conn.prepareStatement(genreQuery);

    String starsQuery = "SELECT s.name, s.id FROM stars_in_movies as sim, stars as s, movies as m WHERE sim.movieId=m.id AND sim.starId=s.id AND m.id=? ORDER BY s.name ASC LIMIT 3";
    PreparedStatement statement3 = conn.prepareStatement(starsQuery);


ResultsServlet.java:

    String prefixQuery = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r WHERE m.id=r.movieId AND m.title LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";
    PreparedStatement prefixStatement = conn.prepareStatement(prefixQuery);

    String genreQuery = "SELECT g.name FROM genres as g, genres_in_movies as gim, movies as m WHERE g.id=gim.genreId AND m.id=? AND m.id=gim.movieId ORDER BY g.name ASC LIMIT 3";
    PreparedStatement statement2 = conn.prepareStatement(genreQuery);

    String starsQuery = "SELECT s.name, s.id FROM stars_in_movies as sim, stars as s, movies as m WHERE sim.movieId=m.id AND sim.starId=s.id AND m.id=? ORDER BY s.name ASC LIMIT 3";
    PreparedStatement statement3 = conn.prepareStatement(starsQuery);

    String genreListQuery = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, genres as g, genres_in_movies as gim, ratings as r WHERE gim.movieId=m.id AND gim.genreId=g.id AND r.movieId=m.id AND g.name=? ORDER BY r.rating DESC LIMIT ? OFFSET ?";
    PreparedStatement genreStatement = conn.prepareStatement(genreListQuery);
    
    String searchQuery = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r, stars_in_movies as sim, stars as s WHERE m.id=r.movieId AND m.title LIKE ? AND m.year LIKE ? AND m.director LIKE ? AND sim.movieId=m.id AND s.id=sim.starId AND s.name LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";
    PreparedStatement searchStatement = conn.prepareStatement(searchQuery);


SingleMovieServlet.java:

    String query3 = "SELECT s.id, s.name FROM stars as s, stars_in_movies as sim WHERE sim.movieId=? AND s.id=sim.starId ORDER BY s.name ASC";
    PreparedStatement statement3 = conn.prepareStatement(query3);

    String query2 = "SELECT g.name FROM genres_in_movies as gim, genres as g WHERE gim.movieId=? AND gim.genreId=g.id ORDER BY g.name ASC";
    PreparedStatement statement2 = conn.prepareStatement(query2);

    String query = "SELECT title, year, director, rating FROM movies as m, ratings as r WHERE m.id=? AND r.movieId=m.id";
    PreparedStatement statement = conn.prepareStatement(query);


SingleStarServlet.java:

    String query = "SELECT name, birthYear FROM stars WHERE id=?";
    PreparedStatement statement = conn.prepareStatement(query);
    
    String query2 = "SELECT m.title, sim.movieId FROM stars_in_movies as sim, movies as m WHERE sim.starId=? AND sim.movieId = m.id ORDER BY m.title ASC";
    PreparedStatement statement2 = conn.prepareStatement(query2);



# Performance Optimizations:
 1. Using batch commits made longest insertion only a minute long  

 2. Kept a hash map of inserted stars/movies, which reduced need for multiple DB connections
 and fewer queries


# Inconsistent Data Report
 54 duplicate movies found in mains243.xml
 47055 castings parsed
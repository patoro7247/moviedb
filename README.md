Video: https://www.youtube.com/watch?v=-ZTHQrnDHF4
SQL LIKE Usage:
    
    SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r, stars_in_movies as sim, stars as s WHERE m.id=r.movieId AND m.title LIKE ? AND m.year LIKE ? AND m.director LIKE ? AND sim.movieId=m.id AND s.id=sim.starId AND s.name LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";
    searchStatement.setString(1, "%"+ searchTitle + "%");
    searchStatement.setString(2, "%"+ searchDirector + "%");
    searchStatement.setString(3, "%"+ searchStar + "%");




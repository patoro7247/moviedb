Use moviedb;

DELIMITER $$

CREATE PROCEDURE add_movie (IN id VARCHAR(10), IN title VARCHAR(100), IN movieYear INT, IN director VARCHAR(100), IN starName VARCHAR(100), IN genreName VARCHAR(32))
BEGIN
    SELECT count(*)
    INTO s_count
    FROM stars
    WHERE name=starName

    IF s_count = 0 THEN
        --insert new star with NULL birthday
        INSERT INTO stars VALUES (starName, 0)
    ELSE


    INSERT INTO movies VALUES (id, title, movieYear, director);
    INSERT INTO ratings VALUES (id, 0, 0);
END
$$

DELIMITER ;

SHOW PROCEDURE STATUS WHERE db = 'moviedb';

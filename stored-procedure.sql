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
        INSERT INTO stars VALUES
    ELSE


    INSERT INTO movies VALUES (id, title, movieYear, director);
    INSERT INTO movies VALUES (id, title, movieYear, director);
END
$$

DELIMITER ;

SHOW PROCEDURE STATUS WHERE db = 'moviedb';

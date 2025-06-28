CREATE DATABASE IF NOT EXISTS NotesDB;
USE NotesDB;

CREATE USER 'prm'@'localhost' IDENTIFIED BY 'prm';
GRANT ALL PRIVILEGES ON NotesDB.* TO 'prm'@'localhost';

CREATE TABLE IF NOT EXISTS Notes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    photoUrl VARCHAR(2083),
    audioUrl VARCHAR(2083)
);

INSERT INTO Notes (id, text, location, photoUrl, audioUrl) VALUES
  ('1', 'Pierwsza notatka', '52.2297,21.0122', NULL, NULL),
  ('2', 'Druga notatka z obrazkiem', '50.0647,19.9450', 'photo1.jpg', NULL),
  ('3', 'Notatka z dźwiękiem', '51.1079,17.0385', NULL, 'audio1.mp3'),
  ('4', 'Pełna notatka', '54.3520,18.6466', 'photo2.jpg', 'http://example.com/audio2.mp3');

DROP TABLE Notes;

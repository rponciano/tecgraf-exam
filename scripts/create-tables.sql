-- criar base de dados
CREATE TABLE STATUS (
	ID	INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	NOME 	VARCHAR(255) NOT NULL,
	UNIQUE(ID)
);

CREATE TABLE USUARIO (
	ID 		INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	LOGIN 		VARCHAR(4) NOT NULL,
	NOMECOMPLETO	VARCHAR(255),
	STATUS		INT NOT NULL,
	GERENCIAATUAL 	VARCHAR(255),
	UNIQUE(ID),
	FOREIGN KEY (STATUS) REFERENCES STATUS(ID)
);

CREATE TABLE PLUGIN (
	ID 		INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	NOME 		VARCHAR(255),
	DESCRICAO 	VARCHAR(255),
	DATACRIACAO 	DATE,
	UNIQUE(ID)
);

CREATE TABLE FUNCIONALIDADE (
	ID 		INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	NOME 		VARCHAR(255),
	DESCRICAO 	VARCHAR(255),
	DATACRIACAO 	DATE,
	PLUGINID 	INT NOT NULL,
	UNIQUE(ID),
	FOREIGN KEY (PLUGINID) REFERENCES PLUGIN(ID)
);

CREATE TABLE LOG (
	ID 		INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	TIPO		VARCHAR(255), 
	MENSAGEM	VARCHAR(255),
	CAUSA		VARCHAR(255),
	UNIQUE(ID)
);

CREATE TABLE PERFIL (
	ID 		INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	NOME		VARCHAR(255), 
	DESCRICAO	VARCHAR(255),
	DATACRIACAO	DATE,
	UNIQUE(ID)
);

CREATE TABLE PERMISSAO (
	ID 		INTEGER 	GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL,
	PERFILID	INTEGER, 
	FUNCID		INTEGER,
	UNIQUE(ID),
	FOREIGN KEY (PERFILID) REFERENCES PERFIL(ID),
	FOREIGN KEY (FUNCID) REFERENCES FUNCIONALIDADE(ID)
);

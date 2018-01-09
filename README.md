# Atom SysVAP

## 1.	Introduction
Atom SysVAP is a tool for development and simulation of finite automata. The name "Atom" is an allusion to the atom, due to the appearance that an automaton can gain in a state diagram. The SysVAP surname is an acronym for "System for Validation of finite Automatons and Execution Plans".

The tool is suitable for learning formal languages and allows integration with other systems through a JAVA library. This integration can be useful in systems that have deliberative skills, to evaluate the actions that will be performed, functioning as a security system.

Atom SysVAP allows representation in Mealy and Moore state machines, in addition to StatesCharts.

## 2.	License

The tool is open source and is protected under the [GNU General Public License v3.0](LICENSE) license.
Permissions of this strong copyleft license are conditioned on making available complete source code of licensed works and modifications, which include larger works using a licensed work, under the same license. Copyright and license notices must be preserved. Contributors provide an express grant of patent rights.

#### In your publications do not forget to mention the work:

##### In Portuguese:
  ```xml
    @MASTERTHESIS {ivoandre;2013,
            author = "Ivo, André A. S.;",
            title  = "Uma ferramenta para avaliação de planos de voo de satélites 
                      usando modelos de estados",
            school = "Instituto Nacional de Pesquisas Espaciais (INPE)",
            year   = "2013",
            type   = "Dissertação (Mestrado em Engenharia e Gerenciamento de Sistemas Espaciais)"
            url    = "http://urlib.net/rep/8JMKD3MGP7W/3FB2RJE?ibiurl.language=pt-BR"
        }
  ```
  
  ##### In English:
  ```xml
    @MASTERTHESIS {ivoandre;2013,
            author = "Ivo, André A. S.;",
            title  = "A tool for evaluating satellite flight plans using state models",
            school = "National Institute of Space Research (INPE)",
            year   = "2013",
            type   = "Master Thesis (MSc in Engineering and Management of Space Systems)"
            url    = "http://urlib.net/rep/8JMKD3MGP7W/3FB2RJE?ibiurl.language=pt-BR"
  ```
## 3. If you prefer download of binary file
The binary file can be downloaded in [Atom](https://github.com/andreivo/Atom/raw/master/binaries/AtomBinaries.rar).

After downloading, unzip the file into a folder and run Atom.jar. If the Java environment variables are configured on your computer, just double-click, otherwise run from the command line:

```bash
java -jar Atom.jar
```
#### Requirements
To run it is necessary to have the java SDK installed.


## 4.	About the source code

The tool is a desktop application and was developed using Java Swing.
At that time, Netbeans IDE 7.0.1 and the Java 6 SDK were used.

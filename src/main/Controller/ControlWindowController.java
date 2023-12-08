package main.Controller;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.graphdb.*;

import main.Data.AdditionalVertexData;
import main.Data.AlignmentBlockAssociation;
import main.Data.Configuration;
import main.Data.Layer;
import main.Data.N4JAlignmentBlockAssociation;
import main.Data.N4JChromosome;
import main.Data.N4JSequenceVerticesData;
import main.Algorithms.GraphProjectionSugiyama;
import main.Data.SequenceVerticesData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControlWindowController {
	
    //Für Dirk
//    private final static String DB_PATH = "/datadisk/zeckzer/Forschung/Projekte/Leipzig/IVDA/Schebera-Jeremias/SuperGenomeBrowser/graph.db";
    //Für Jeremias
//    private final static String DB_PATH = "/home/jeremias/Schreibtisch/Fabian_data/new DB/graph.db";
	
	//private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
//    private List<TableViewEntry> selections = new ArrayList<>();

    private Graph graphDB;
    private Cluster cluster;
    private Client client;
    private Vertex guideSubStructure;
    private String guideSubStructureName;
    private Vertex guideGenome;
    private int minPositionOnSubStructure;
    private int maxPositionOnSubStructure;
    boolean circleIsHovered = false;
    
    private Driver driver;
    private String database;
    private Map<String, Node> n4JGenomeVertexAssociation;
    private Node n4JGuideGenome;
    private Map<String, Node> n4JSubStructureVertexAssociation;
    private Node n4JGuideSubStructure;
    private int n4JMinPositionOnSubStructure;
    private int n4JMaxPositionOnSubStructure;
    private List<Node> n4JTemp_guidePath;
    private List<Node> n4JGuidePath;
    private N4JAlignmentBlockAssociation n4JAlignmentBlockAssociation;
    private HashMap<Node, AdditionalVertexData> n4JAdditionalVertexData;
    private N4JSequenceVerticesData n4JSequenceVerticesData;
    private HashMap<String, Node> n4JCompareableSubStructuresVertexAssociation;
    private Stream<Record> n4JStream;
    private List<Node> n4JSequence;
    
    private Map<String, Vertex> genomeVertexAssociation;
    private Map<String, Vertex> subStructureVertexAssociation;
    private HashMap<String, Vertex> compareableSubStructuresVertexAssociation;
    private HashMap<String, HashMap<String, Long>> compareableSubStructuresVertexAssociationNew;
    private List<Vertex> temp_guidePath;
    private List<Vertex> guidePath;
    private SequenceVerticesData sequenceVerticesData;
//    private Map<String, TableViewEntry> selectedCompareOptions;
//    private List<Rectangle> lastSelectedRectangle = new ArrayList<>();
//    private Group vertexTextGroup;
    private Set<Vertex> subStructuresToDraw;
    private AlignmentBlockAssociation alignmentBlockAssociation;
    private int lengthFilter;
    private GraphProjectionSugiyama sugiyamaProjection;
    private HashMap<Vertex, AdditionalVertexData> additionalVertexData;
    private List<Vertex> sequence;
    private Set<Vertex> checkVertexExist;
    private Stream<Result> stream;
    
    //private String newText;
    //private String existingText;
    
    private Configuration configuration;
    private Configuration reorderConfiguration;
    
    public static final String superGenomeIdentifiere = "Super_Genome";
//    private final int spaceFactor = 2;
    
    public GraphProjectionSugiyama getSugiyamaProjection() {
    	return sugiyamaProjection;
    }
    
    
    
//    public boolean isJoinCheckBoxSelected() {
//        return joinCheckBox.isSelected();
//    }
//
//    public void setParent(MainWindowController parent) {
//        this.parent = parent;
//    }
//
//    public void clearLastSelectedCircle() {
//        lastSelectedRectangle.clear();
//    }


//    @OverrideInteger
//    public void initialize(URL location, ResourceBundle resources) {
//        this.initStatus();
//
//        //Change-Listener if (another) species in the ComboBox of Species is selcted --> fill Chromosome ComboBox
//        //2. Step
//        comboBoxSpecies.getSelectionModel().selectedIndexProperty().addListener(
//            (ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
//                comboBoxSpeciesSelected(newValue);
//            });
//
//        //Change-Listener if (another) chromosome in ComboBox of Chromosomes is selected
//        //3. Step
//        comboBoxStructure.getSelectionModel().selectedIndexProperty().addListener(
//            (ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) -> {
//                comboBoxStructureSelected(newNumber);
//            });
//
//        //Change-Listener if text in Start range textfield is changed
//        //4. Step
//        txtFldStart.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldString, String newString) -> {
//            textChanged();
//        });
//
//        //Change-Listener if text in End range textfield is changed
//        //also 4. Step
//        txtFldEnd.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldString, String newString) -> {
//            textChanged();
//        });
//
//        //Change-Listener if text in min block length textfield is changed
//        //also 4. Step
//        txtFldMinLength.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldString, String newString) -> {
//            textChanged();
//        });controller
//
//        //Change-Listener if entries changed in TableView
//        //
//        tableOtherChromosomeOrder.getItems().addListener(new ListChangeListener<TableViewEntry>() {
//            @Override
//            public void onChanged(javafx.collections.ListChangeListener.Change<? extends TableViewEntry> pChange) {
//                tableOtherChromosomeOrderChanged(pChange);
//            }
//        });
//
//        treeViewOtherChromosomesAndSpecies.getSelectionModel().selectedIndexProperty().addListener(
//            (ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
//                if (number2.intValue() == 0) {
//                    tableOtherChromosomeOrder.setDisable(true);
//                }
//            });
//    }

//    private void comboBoxSpeciesSelected(Number newValue) {
//        if ((int) newValue != -1) {
//            comboBoxStructure.getSelectionModel().clearSelection();
//            guideSpecies = speciesVertexAssociation.get(comboBoxSpecies.getItems().get((Integer) newValue).toString());
//            getChromosomes();
//            ObservableList<String> chromosomeNames = FXCollections.observableArrayList(chromosomesVertexAssociation.keySet());
//            if (chromosomeNames.size() > 0) {
//                comboBoxStructure.setDisable(false);
//                comboBoxStructure.setItems(chromosomeNames);
//            }
//        } else {
//            comboBoxStructure.setItems(FXCollections.observableArrayList());
//            comboBoxStructure.setDisable(true);
//        }
//    }
//
//    private void comboBoxStructureSelected(Number newNumber) {
//        if ((int) newNumber != -1) {
//            guideStructure = chromosomesVertexAssociation.get(comboBoxStructure.getItems().get((Integer) newNumber).toString());
//            getChromosomeBoundaries();
//            lblRange.setText("Gib einen Bereich zwischen " + minPositionOnChromosome + " und " + maxPositionOnChromosome + " an");
//            txtFldStart.setDisable(false);
//            txtFldEnd.setDisable(false);
//            txtFldMinLength.setDisable(false);
//        } else {
//            txtFldStart.setDisable(true);
//            txtFldEnd.setDisable(true);
//            txtFldStart.setText("");
//            txtFldEnd.setText("");
//            txtFldMinLength.setDisable(true);
//            txtFldMinLength.setText("");
//        }
//    }
//
//    private void tableOtherChromosomeOrderChanged(ListChangeListener.Change<? extends TableViewEntry> pChange) {
//        System.out.println(pChange.getList().size());
//        while (pChange.next()) {
//            // Do your changes here
//            if (tableOtherChromosomeOrder.getItems().size() > 0) {
//                btnPaintGraph.setDisable(false);
//                joinCheckBox.setDisable(false);
//            } else {
//                btnPaintGraph.setDisable(true);
//                joinCheckBox.setDisable(true);
//            }
//        }
//    }

    //Set the elements to the initial status --> disable and clear them
    //0. Step
//    private void initStatus() {
//        comboBoxSpecies.setItems(FXCollections.observableArrayList());
//        comboBoxSpecies.setDisable(true);
//        comboBoxStructure.setItems(FXCollections.observableArrayList());
//        comboBoxStructure.setDisable(true);
//        txtFldEnd.setDisable(true);
//        txtFldStart.setDisable(true);
//        txtFldMinLength.setDisable(true);
//        lblRange.setText("");
//        btnLoadGuideSequence.setDisable(true);
//        btnPaintGraph.setDisable(true);
//        joinCheckBox.setDisable(true);
//
//        labelSpaceFactor.setText("Space: " + sliderSpaceFactor.getValue());
//        labelThicknessFactor.setText("Thickness: " + sliderThicknessFactor.getValue());
//
//        treeViewDrawedChromosomes.setCellFactory(CheckBoxTreeCell.<Layer>forTreeView());
//        treeViewDrawedChromosomes.setDisable(true);
//        treeViewOtherChromosomesAndSpecies.setCellFactory(CheckBoxTreeCell.<Layer>forTreeView());
//        treeViewOtherChromosomesAndSpecies.setDisable(true);
//        tableOtherChromosomeOrder.setDisable(true);
//
//        if (tableOtherChromosomeOrder.getColumns().isEmpty()) {
//            tableOtherChromosomeOrder.getColumns().add(createCol("Genome", TableViewEntry::speciesProperty, 150));
//            tableOtherChromosomeOrder.getColumns().add(createCol("Sub-Structure", TableViewEntry::chromosomeProperty, 150));
//            tableOtherChromosomeOrder.getColumns().add(createCol("Mark", TableViewEntry::chromosomeProperty, 50));
//            tableOtherChromosomeOrder.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        }
//
//        // !!!Test!!!
////        loadFileChooser();
////        txtFldStart.setText("7744");
////        txtFldEnd.setText("9984");
////        txtFldMinLength.setText("20");
//        // !!!Test!!!
//    }

    public Configuration getConfiguration() {
		return configuration;
	}



	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}



	//Klick on Open Button --> to Load Graph DB
    //1. Step
    public List<String> loadDBTinkerpop(String db_path){
        //it is allready a graphDB-DB connected --> loading new graphDB-DB
        //close existing connection and set initial status
        //stopDB();
//        if (graphDB != null) {
//            initStatus();
//        }

//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setTitle("Select Neo4j Database");
//        File file = directoryChooser.showDialog(MainController.getPrimaryStage());
//        if (file != null){
        	System.out.println("Connect to Gremlin Server");
            //graphDB = Neo4jGraph.open(db_path);
        	//Cluster cluster = Cluster.open();
			
        	Cluster.Builder builder = Cluster.build();
        	builder.addContactPoint("localhost"); //builder.addContactPoint("gremlin-server"); 
			builder.port(8182); 
			Cluster cluster = builder.create();
			//cluster = Cluster.open();
			client = cluster.connect();
						 
////        directoryChooser.setInitialDirectory(new File("/home/jeremias/Schreibtisch/Fabian_data/new DB"));
//        String dbpath = directoryChooser.showDialog(MainController.getPrimaryStage()).getPath();
            //!!!Testzwecke!!!
//        graphDB = Neo4jGraph.open(DB_PATH);
            //!!!Testzwecke!!!
            //System.out.println("Load DB Traversal");
            //graphTraversal = graphDB.traversal();
            //GraphTraversalSource graphTraversal =
          	      //EmptyGraph.instance().traversal().withRemote(DriverRemoteConnection.using(cluster));
						
            getGenomes();
            return new ArrayList<>(genomeVertexAssociation.keySet());
//            if (speciesNames.size() > 0) {
//                comboBoxSpecies.setDisable(false);
//                comboBoxSpecies.setItems(speciesNames);
//            }
//        } else {
//            System.out.println("No File selected");
//        }
    }
    
    public List<String> loadDBNeo4J(String database){
    	this.database = database;
    	//For Docker 
    	driver = GraphDatabase.driver("bolt://neo4j:7687", AuthTokens.basic("jkgs", "testtest"));
    	//For Local Running 
    	//driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("jkgs", "testtest"));
    	n4JGetGenomes();
    	return new ArrayList<>(n4JGenomeVertexAssociation.keySet());
    }
    
    private Session getSession() {
        if (database == null || database.isEmpty()) return driver.session();
        return driver.session(SessionConfig.forDatabase(database));
    }
    
    //Query to get all Species-Vertices from graph DB
    //Part of 1. Step
    private void n4JGetGenomes() {
    	System.out.println("Get genomes");
    	n4JGenomeVertexAssociation = new HashMap<>();
    	try (Session session = getSession()) {
    	    org.neo4j.driver.Result result = session.run("Match (species:Species)-[:hasChr]->(subStructure:Chromosome) RETURN species, count(subStructure) as numberSubStructures");
    	    while (result.hasNext()) {
    	    	Record record = result.next();
    	    	org.neo4j.driver.types.Node vertex = record.get("species").asNode();
    	    	int numberSubStructures = record.get("numberSubStructures").asInt();
    	    	n4JGenomeVertexAssociation.put(vertex.get("name").asString() + " [" + numberSubStructures + "]", vertex);
    	    }
    	}
    }
    
    //Query to get all Species-Vertices from graph DB
    //Part of 1. Step
    private void getGenomes() {
    	System.out.println("Get genomes");
    	genomeVertexAssociation = new HashMap<>();
    	ResultSet results = client.submit("g.V().hasLabel(\"Species\""
				+ "        ).project("
				+ "            \"vertex\", \"name\""
				+ "        ).by(__.identity()"
				+ "        ).by(__.values(\"name\"));");
    	
		Iterator<Result> speciesIterator =  results.iterator();
		while(speciesIterator.hasNext()) {
			Result r = speciesIterator.next();
			Map<String, Object> specie = (Map<String, Object>) r.getObject();
			genomeVertexAssociation.put(specie.get("name").toString(), (Vertex) specie.get("vertex"));
		}
		
//        GraphTraversal<Vertex, Map<String, Object>> traversalSpecies = graphTraversal.V().hasLabel("Species"
//        ).project(
//            "vertex", "name"
//        ).by(__.identity()
//        ).by(__.values("name"));
//        while (traversalSpecies.hasNext()) {
//            Map<String, Object> specie = traversalSpecies.next();
//            genomeVertexAssociation.put(specie.get("name").toString(), (Vertex) specie.get("vertex"));
//        }
    }
    
    //Query to get all Chromosome-Vertices from graph DB
    //Part of 2. Step
    public List<String> n4JGetSubStructureList(String selectedSpecies) {
    	n4JGuideGenome = n4JGenomeVertexAssociation.get(selectedSpecies);
    	System.out.println("Get substructures");
    	n4JSubStructureVertexAssociation = new HashMap<>();
    	Map<String,Object> params = new HashMap<>();
    	params.put("n4JGuideGenomeId", n4JGuideGenome.get("id"));
    	try (Session session = getSession()) {
    	    org.neo4j.driver.Result result = session.run("MATCH (subStructure:Chromosome)<-[:hasChr]-(:Species {id:$n4JGuideGenomeId}) Return subStructure, subStructure.length as subStructureLength", params);
    	    while (result.hasNext()) {
    	    	Record record = result.next();
    	    	org.neo4j.driver.types.Node vertex = record.get("subStructure").asNode();
    	    	System.out.println(record.get("subStructureLength").getClass());
    	    	int subStructureLength;
    	    	if (record.get("subStructureLength").getClass().toString().endsWith("IntegerValue")) {
    	    		subStructureLength = record.get("subStructureLength").asInt();
    	    	} else {
    	    		subStructureLength = Integer.valueOf(record.get("subStructureLength").asString());
    	    	}
    	    	
    	    	n4JSubStructureVertexAssociation.put(vertex.get("name").asString() + " [" + subStructureLength  + "]", vertex);
    	    }
    	}
    	return new ArrayList<>(n4JSubStructureVertexAssociation.keySet());
    }
    
    public List<String> getSubStructureList(String selectedSpecies) {
    	guideGenome = genomeVertexAssociation.get(selectedSpecies);
    	getSubstructures();
    	return new ArrayList<>(subStructureVertexAssociation.keySet());
    }
    
    //Query to get all Chromosome-Vertices from graph DB
    //Part of 2. Step
    private void getSubstructures() {
    	
    	
    	//!!!!!!!!!!!!!		Query for Substructures with only thrown sequences		!!!!!!!!!!!!!!!!! 
    	//g.V().hasLabel("Species").out("hasChr").not(__.in("isOn").values("throw").is(false)).as("chr").in("hasChr").as("spe").select("spe", "chr").by("name").by("name")
    	//!!!!!!!!!!!!!		Query for Substructures with only thrown sequences		!!!!!!!!!!!!!!!!! 
    	
    	System.out.println("Get substructures");
    	subStructureVertexAssociation = new HashMap<>();
    	Map<String,Object> params = new HashMap<>();
    	params.put("guideGenome", guideGenome.id().toString());
    	ResultSet results = client.submit("g.V(guideGenome"
    			+ "        ).out(\"hasChr\""
    			+ "		   ).not(__.not(__.in(\"isOn\").has(\"throw\", false))"
    			+ "        ).project(\"vertex\", \"name\""
    			+ "        ).by(__.identity()"
    			+ "        ).by(__.values(\"name\"))", params);
    	
    	Iterator<Result> subStructureIterator =  results.iterator();
		while(subStructureIterator.hasNext()) {
			Result r = subStructureIterator.next();
			Map<String, Object> subStructure = (Map<String, Object>) r.getObject();
			subStructureVertexAssociation.put(subStructure.get("name").toString(), (Vertex) subStructure.get("vertex"));
		}
    	
//        while (traversalChromsomes.hasNext()) {
//            Map<String, Object> chromosome = traversalChromsomes.next();
//            subStructureVertexAssociation.put(chromosome.get("name").toString(), (Vertex) chromosome.get("vertex"));
//        }
    }
    
    //Query to get the min- and max-position in chromosome from GraphDB
    //also 3. Step
    public int[] n4JGetSubstructureBoundaries(String selectedSubStructure) {
    	System.out.println("Get Boundaries");
    	n4JGuideSubStructure = n4JSubStructureVertexAssociation.get(selectedSubStructure);
    	
    	Map<String,Object> params = new HashMap<>();
    	params.put("n4JGuideSubStructureId", n4JGuideSubStructure.get("id"));
    	try (Session session = getSession()) {
    	    org.neo4j.driver.Result minResult = session.run("MATCH (seq:Sequence)-[:isOn]->(chr:Chromosome {id:$n4JGuideSubStructureId}) WHERE seq.throw = false WITH min(seq.start) as min RETURN min", params);
    	    Record minRecord = minResult.next();
    	    n4JMinPositionOnSubStructure = minRecord.get("min").asInt();
    	}
    	try (Session session = getSession()) {
    		org.neo4j.driver.Result maxResult = session.run("MATCH (seq:Sequence)-[:isOn]->(chr:Chromosome {id:$n4JGuideSubStructureId}) WHERE seq.throw = false WITH max(seq.start + seq.length) as max RETURN max", params);
    	    Record maxRecord = maxResult.next();
    	    n4JMaxPositionOnSubStructure = maxRecord.get("max").asInt();
    	}
    	
    	int boundaries[] = {n4JMinPositionOnSubStructure, n4JMaxPositionOnSubStructure};
        return boundaries;
    }
    
    //Query to get the min- and max-position in chromosome from GraphDB
    //also 3. Step
    //TO-DO: Make it smoother...schöner machen
    public int[] getSubstructureBoundaries(String selectedSubStructure) {
    	System.out.println("Get Boundaries");
    	guideSubStructure = subStructureVertexAssociation.get(selectedSubStructure);
    	Map<String,Object> params = new HashMap<>();
    	params.put("guideSubStructure", guideSubStructure.id().toString());
    	ResultSet results = client.submit("g.V(guideSubStructure).as(\"chr\").in(\"isOn\").not(__.in(\"nextSeq\")).until(__.has(\"throw\", false)).repeat(__.out(\"nextSeq\")).values(\"start\").as(\"min_pos\").select(\"chr\").in(\"isOn\").not(__.out(\"nextSeq\")).until(__.has(\"throw\", false)).repeat(__.in(\"nextSeq\")).project(\"start\", \"length\").by(\"start\").by(\"length\").math(\"start + length - 1\").as(\"max_pos\").select(\"min_pos\", \"max_pos\").next()", params);
        //Map<String, Object> range = graphTraversal.V(guideSubStructure).as("chr").in("isOn").not(__.in("nextSeq")).until(__.has("throw", false)).repeat(__.out("nextSeq")).values("start").as("min_pos").select("chr").in("isOn").not(__.out("nextSeq")).until(__.has("throw", false)).repeat(__.in("nextSeq")).project("start", "length").by("start").by("length").math("start + length - 1").as("max_pos").select("min_pos", "max_pos").next();
    	Iterator<Result> boundarieIterator =  results.iterator();
		while(boundarieIterator.hasNext()) {
			Result r = boundarieIterator.next();
			if (((SimpleEntry<String, Object>) r.getObject()).getKey().equals("min_pos")) {
				SimpleEntry<String, Integer> boundrie = (SimpleEntry<String, Integer>) r.getObject();
				System.out.println(boundrie);
				minPositionOnSubStructure = boundrie.getValue();
			} else {
				SimpleEntry<String, Double> boundrie = (SimpleEntry<String, Double>) r.getObject();
				System.out.println(boundrie);
				maxPositionOnSubStructure = boundrie.getValue().intValue();
			}
		}
    	
//    	minPositionOnSubStructure = (Integer) range.get("min_pos");
//        maxPositionOnSubStructure = ((Double) range.get("max_pos")).intValue();
        
        int boundaries[] = {minPositionOnSubStructure, maxPositionOnSubStructure};
        
        return boundaries;
    }

    //Klick on Load Guide Sequence Button --> to query the Guide Sequence from the GraphDB
    //5. Step
    public int[] n4JLoadGuideSequence(int start_range, int end_range) {
    	System.out.println("Load GS");
    	int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        n4JAdditionalVertexData = new HashMap<Node, AdditionalVertexData>();
    	n4JTemp_guidePath = new LinkedList<>();
    	n4JAlignmentBlockAssociation = new N4JAlignmentBlockAssociation();
    	
    	Map<String,Object> params = new HashMap<>();
    	params.put("n4JGuideSubStructureId", n4JGuideSubStructure.get("id"));
    	params.put("start_range", start_range);
    	params.put("end_range", end_range);
    	    			
		try (Session session = getSession()) {
    	    org.neo4j.driver.Result result = session.run("MATCH (seq:Sequence)-[:isOn]->(chr:Chromosome {id:$n4JGuideSubStructureId}), (seq:Sequence)<-[:hasSeq]-(block:Block)<-[:containsBlock]-(vertex:Vertex) WHERE seq.throw = false AND seq.start <= $end_range AND seq.start + seq.length - 1 >= $start_range RETURN block, vertex ORDER BY seq.start", params);
    	    List<Record> resultList = result.list();
    	    for (Record record : resultList) {
    	    	org.neo4j.driver.types.Node block = record.get("block").asNode();
    	    	org.neo4j.driver.types.Node vertex = record.get("vertex").asNode();
    	    	n4JTemp_guidePath.add(vertex);
    	    	n4JAlignmentBlockAssociation.add(vertex, block);
    	    	
    	    	int currentLength = n4JAlignmentBlockAssociation.getLength(vertex);
                if (currentLength < minLength) {
                	minLength = currentLength;
                }
                if (currentLength > maxLength) {
                	maxLength = currentLength;
                }
               
    	    }
    	    n4JAddAdditionalVertexData(n4JTemp_guidePath);
    	}
		
		int extremes[] = {minLength, maxLength};
        
        return extremes;
    }

    //Klick on Load Guide Sequence Button --> to query the Guide Sequence from the GraphDB
    //5. Step
    public int[] loadGuideSequence(int start_range, int end_range) {
    	System.out.println("Load GS");
        //preparation
    	temp_guidePath = new LinkedList<>();
        alignmentBlockAssociation = new AlignmentBlockAssociation();
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        additionalVertexData = new HashMap<Vertex, AdditionalVertexData>();
        
        //query
        Map<String,Object> params = new HashMap<>();
    	params.put("guideSubStructure", guideSubStructure.id().toString());
    	params.put("start_range", start_range);
    	params.put("end_range", end_range);
    	ResultSet results = client.submit("g.V(guideSubStructure"
    			+ "        ).in(\"isOn\").has(\"throw\", false"
    			+ "        ).has(\"start\", P.lte(end_range)"
    			+ "        ).where(\n"
    			+ "                __.project(\"start\", \"length\""
    			+ "                ).by(\"start\"\n"
    			+ "                ).by(\"length\"\n"
    			+ "                ).math(\"start + length - 1\""
    			+ "                ).is(P.gte(start_range))"
    			+ "        ).order().by(\"start\"\n"
    			+ "        ).in(\"hasSeq\").in(\"containsBlock\""
    			+ "        ).dedup().project(\"vertex\", \"block\""
    			+ "        ).by(__.identity()\n"
    			+ "        ).by(__.out(\"containsBlock\").dedup())", params);
//        GraphTraversal<Vertex, Map<String, Object>> traversalChromsomes = graphTraversal.V(guideSubStructure
//        ).in("isOn").has("throw", false
//        ).has("start", P.lte(end_range)
//        ).where(
//                __.project("start", "length"
//                ).by("start"
//                ).by("length"
//                ).math("start + length - 1"
//                ).is(P.gte(start_range))
//        ).order().by("start"
//        ).in("hasSeq").in("containsBlock"
//        ).dedup().project("vertex", "block"
//        ).by(__.identity()
//        ).by(__.out("containsBlock").dedup());
    	
    	Iterator<Result> guideSeqIterator =  results.iterator();
		while(guideSeqIterator.hasNext()) {
			Result r = guideSeqIterator.next();
			Map<String, Object> entry = (Map<String, Object>) r.getObject();     
			
			Vertex vertex = (Vertex) entry.get("vertex");
            Vertex alignmentBlock = (Vertex) entry.get("block");
            temp_guidePath.add(vertex);
            alignmentBlockAssociation.add(vertex, alignmentBlock);
            
            int currentLength = alignmentBlockAssociation.getLength(vertex);
            if (currentLength < minLength) {
            	minLength = currentLength;
            }
            if (currentLength > maxLength) {
            	maxLength = currentLength;
            }
           
            addAdditionalVertexData(vertex);
		}
//        while (traversalChromsomes.hasNext()) {
//            Map<String, Object> entry = traversalChromsomes.next();
//            Vertex vertex = (Vertex) entry.get("vertex");
//            Vertex alignmentBlock = (Vertex) entry.get("block");
//            //if ((Integer) alignmentBlock.value("length") >= Integer.valueOf(txtFldMinLength.getText())) {
//            temp_guidePath.add(vertex);
//            alignmentBlockAssociation.add(vertex, alignmentBlock);
//            
//            int currentLength = alignmentBlockAssociation.getLength(vertex);
//            if (currentLength < minLength) {
//            	minLength = currentLength;
//            }
//            if (currentLength > maxLength) {
//            	maxLength = currentLength;
//            }
//            //}
//            addAdditionalVertexData(vertex);
//        }

        int extremes[] = {minLength, maxLength};
        
        return extremes;
    } 
    
    //Fill the TreeView with the compareable Chromosomes
    //Part of 5. Step
    public HashMap<String, HashMap<String,Long>> n4JGetComparableChromosomesAndSpecies(int lengthFilter) {
    	System.out.println("Load CS");
    	this.lengthFilter = lengthFilter;
    	n4JGuidePath = new ArrayList<Node>();
    	//String text = "[";
//    	String text2 = "[";
    	for (Node vertex : n4JTemp_guidePath) {
//    		text2 = text2 + vertex.id() + ", ";
    		if (n4JAlignmentBlockAssociation.getLength(vertex) >= lengthFilter) {
    			n4JGuidePath.add(vertex);
    			//text = text + vertex.get("id") + ", ";
    		}
    	}
//    	text = text.substring(0, text.length() - 2);
//    	text = text + "]";
////    	text2 = text2.substring(0, text2.length() - 2);
////    	text2 = text2 + "]";
////    	System.out.println(text2);
//    	System.out.println(text);
    	
    	n4JCompareableSubStructuresVertexAssociation = new HashMap<>();
        compareableSubStructuresVertexAssociationNew = new HashMap<String, HashMap<String,Long>>();
        
        //Super Genome --> is always compareable
        //query the comparable Chromosomes from graphDB
        try (Session session = getSession()) {
    	    org.neo4j.driver.Result result = session.run("MATCH (order:Order) RETURN order");
    	    while (result.hasNext()) {
    	    	Record record = result.next();
    	    	org.neo4j.driver.types.Node superGenomeVertex = record.get("order").asNode();  
    	    	String superGenomeText = superGenomeIdentifiere + superGenomeVertex.get("id").toString();
    	    	String superGenomeTextWhole = superGenomeText +  " (" + superGenomeIdentifiere + ")";
    	    	n4JCompareableSubStructuresVertexAssociation.put(superGenomeTextWhole, superGenomeVertex);
                if (!compareableSubStructuresVertexAssociationNew.containsKey(superGenomeIdentifiere)) {
                	compareableSubStructuresVertexAssociationNew.put(superGenomeIdentifiere, new HashMap<String, Long>());
                }
                compareableSubStructuresVertexAssociationNew.get(superGenomeIdentifiere).put(superGenomeText, Long.valueOf(0));
    	    }
    	}
        
        //query the comparable Chromosomes from graphDB
  		Map<String,Object> params = new HashMap<>();
      	params.put("guidePath", n4JGuidePath.stream().map(vertex -> vertex.get("id").asInt()).collect(Collectors.toList()));
      	//System.out.println(params.get("guidePath"));
      	try (Session session = getSession()) {
    	    org.neo4j.driver.Result result = session.run("MATCH (v:Vertex)-[:containsBlock]->(:Block)-[:hasSeq]->(:Sequence)-[:isOn]->(subStructure:Chromosome)<-[:hasChr]-(species:Species) WHERE v.id IN $guidePath RETURN subStructure, species, count(v) as sharedGuidePath", params);
    	    while (result.hasNext()) {
    	    	Record record = result.next();
    	    	org.neo4j.driver.types.Node subStructureVertex = record.get("subStructure").asNode();  
    	    	org.neo4j.driver.types.Node speciesVertex = record.get("species").asNode(); 
    	    	int sharedGuidePath = record.get("sharedGuidePath").asInt(); 
//    	    	System.out.println(subStructure.get("name").toString() + " - " + subStructureVertex.value("name") + ": " + result.getValue());
    			if (!subStructureVertex.equals(n4JGuideSubStructure)) {
    	            String genomeName = speciesVertex.get("name").toString().replaceAll("\"", "");
    	            String subStructureName = subStructureVertex.get("name").toString().replaceAll("\"", "") + " [" + sharedGuidePath + "]";
    	            n4JCompareableSubStructuresVertexAssociation.put(subStructureName + " (" + genomeName + ")", subStructureVertex);
    	            
    	            if (!compareableSubStructuresVertexAssociationNew.containsKey(genomeName)) {
    	            	compareableSubStructuresVertexAssociationNew.put(genomeName, new HashMap<String, Long>());
    	            }
    	            compareableSubStructuresVertexAssociationNew.get(genomeName).put(subStructureName, Long.valueOf(sharedGuidePath));
                }
    	    }
    	}
        
        return compareableSubStructuresVertexAssociationNew;
    }
    
    //Fill the TreeView with the compareable Chromosomes
    //Part of 5. Step
    public HashMap<String, HashMap<String,Long>> getComparableChromosomesAndSpecies(int lengthFilter) {
    	System.out.println("Load CS");
    	this.lengthFilter = lengthFilter;
    	guidePath = new ArrayList<Vertex>();
    	String text = "[";
//    	String text2 = "[";
    	for (Vertex vertex : temp_guidePath) {
//    		text2 = text2 + vertex.id() + ", ";
    		if (alignmentBlockAssociation.getLength(vertex) >= lengthFilter) {
    			guidePath.add(vertex);
    			text = text + vertex.id() + ", ";
    		}
    	}
    	text = text.substring(0, text.length() - 2);
    	text = text + "]";
//    	text2 = text2.substring(0, text2.length() - 2);
//    	text2 = text2 + "]";
//    	System.out.println(text2);
    	//System.out.println(text);
    	
        compareableSubStructuresVertexAssociation = new HashMap<>();
        compareableSubStructuresVertexAssociationNew = new HashMap<String, HashMap<String,Long>>();

        //Super Genome --> is always compareable
        //query the comparable Chromosomes from graphDB
    	ResultSet results = client.submit("g.V().hasLabel(\"Order\")");
    	Iterator<Result> superGenomeIterator =  results.iterator();
		while(superGenomeIterator.hasNext()) {
			Result r = superGenomeIterator.next();
			Vertex superGenomeVertex = r.getVertex();
            String superGenomeText = superGenomeIdentifiere + superGenomeVertex.value("id").toString();
            String superGenomeTextWhole = superGenomeText +  " (" + superGenomeIdentifiere + ")";
            compareableSubStructuresVertexAssociation.put(superGenomeTextWhole, superGenomeVertex);
            if (!compareableSubStructuresVertexAssociationNew.containsKey(superGenomeIdentifiere)) {
            	compareableSubStructuresVertexAssociationNew.put(superGenomeIdentifiere, new HashMap<String, Long>());
            }
            compareableSubStructuresVertexAssociationNew.get(superGenomeIdentifiere).put(superGenomeText, Long.valueOf(0));
		}
    	
//        GraphTraversal<Vertex, Vertex> traversalSuperGenome = graphTraversal.V().hasLabel("Order");
//        //fill TreeView with Super Genome Orders
//        while (traversalSuperGenome.hasNext()) {
//            Vertex superGenomeVertex = traversalSuperGenome.next();
//            String superGenomeText = superGenomeIdentifiere + superGenomeVertex.value("id").toString();
//            compareableSubStructuresVertexAssociation.put(superGenomeText, superGenomeVertex);
//        }

        //query the comparable Chromosomes from graphDB
    	
    	/*text = "[";
    	for (Vertex v : guidePath) {
    		text = text + v.id() + ", ";
    	}
    	System.out.println(text.substring(0, text.length() - 2) + "]");*/
    	
		results = client.submit("g.V(guidePath\n"
				+ "        ).out(\"containsBlock\").dedup().out(\"hasSeq\").out(\"isOn\").as(\"chromosome\"\n"
				+ "        ).in(\"hasChr\").as(\"species\"\n"
				+ "        ).select(\"species\", \"chromosome\").by(\"name\").by(__.identity()\n"
				+ "        ).groupCount().unfold()", params);
//		GraphTraversal<Vertex, Map<String, Object>> traversalChromosomes = graphTraversal.V(guidePath
//        ).out("containsBlock").dedup().out("hasSeq").out("isOn").as("chromosome"
//        ).in("hasChr").as("species"
//        ).select("species", "chromosome").by("name").by(__.identity()
//        ).dedup();

        //fill TreeView with other Chromosomes
		Iterator<Result> comparableChrIterator =  results.iterator();
		while(comparableChrIterator.hasNext()) {
			Result r = comparableChrIterator.next();
			//System.out.println(r.getObject().getClass()	);
			SimpleEntry<Map<String, Object>, Long> result = (SimpleEntry<Map<String, Object>, Long>) r.getObject();
			//System.out.println(result.getValue());
			Map<String, Object> entryKey = (Map<String, Object>) result.getKey();
			Vertex subStructureVertex = (Vertex) entryKey.get("chromosome");
			//System.out.println(entryKey.get("species").toString() + " - " + subStructureVertex.value("name") + ": " + result.getValue());
			if (!subStructureVertex.equals(guideSubStructure)) {
	            String genomeName = entryKey.get("species").toString();
	            String subStructureName = subStructureVertex.value("name").toString() + " [" + result.getValue() + "]";
	            compareableSubStructuresVertexAssociation.put(subStructureName + " (" + genomeName + ")", subStructureVertex);
	            
	            if (!compareableSubStructuresVertexAssociationNew.containsKey(genomeName)) {
	            	compareableSubStructuresVertexAssociationNew.put(genomeName, new HashMap<String, Long>());
	            }
	            compareableSubStructuresVertexAssociationNew.get(genomeName).put(subStructureName, result.getValue());
            }
			/*for (Object entryObject : result.keySet().toArray()) {
				Map<String, Object> entryKey = (Map<String, Object>) entryObject;
				Vertex subStructureVertex = (Vertex) entryKey.get("chromosome");
				if (!subStructureVertex.equals(guideSubStructure)) {
		            String genomeName = entryKey.get("species").toString();
		            String subStructureName = subStructureVertex.value("name").toString() + " [" + result.get(entryKey) + "]";
		            compareableSubStructuresVertexAssociation.put(subStructureName + " (" + genomeName + ")", subStructureVertex);
		            
		            if (!compareableSubStructuresVertexAssociationNew.containsKey(genomeName)) {
		            	compareableSubStructuresVertexAssociationNew.put(genomeName, new HashMap<String, Long>());
		            }
		            compareableSubStructuresVertexAssociationNew.get(genomeName).put(subStructureName, result.get(entryKey));
	            }
			}*/
		}
//        while (traversalChromosomes.hasNext()) {
//            Map nextEntry = traversalChromosomes.next();
//            Vertex subStructureVertex = (Vertex) nextEntry.get("chromosome");
//            if (!subStructureVertex.equals(guideSubStructure)) {
//	            String genomeName = nextEntry.get("species").toString();
//	            String subStructureName = subStructureVertex.value("name").toString();
//	            compareableSubStructuresVertexAssociation.put(subStructureName + " (" + genomeName + ")", subStructureVertex);
//            }
//        }
        
        //return new ArrayList<>(compareableSubStructuresVertexAssociation.keySet()); 
		return compareableSubStructuresVertexAssociationNew;

    }
    
    private void n4JFillSequence(Record record) {
    	if (n4JSequence == null) {
    		n4JSequence = new ArrayList<Node>();
    		//checkVertexExist = new HashSet<>();
    	}
    	
    	org.neo4j.driver.types.Node vertex = record.get("v").asNode(); 
    	org.neo4j.driver.types.Node block = record.get("block").asNode(); 
    	
    	if (block.get("length").asInt() >= lengthFilter) {        	
        	n4JAlignmentBlockAssociation.add(vertex, block);
    		n4JSequence.add(vertex);
        }
    }
    
    private void fillSequence(Result r) {
    	
    	if (sequence == null) {
    		sequence = new ArrayList<Vertex>();
    		//checkVertexExist = new HashSet<>();
    	}
    	
    	Map<String, Object> entry = (Map<String, Object>) r.getObject();
		Vertex vertex = (Vertex) entry.get("vertex");
		//System.out.println(vertex);
        Vertex alignmentBlock = (Vertex) entry.get("block");
        
        if ((Integer) alignmentBlock.value("length") >= lengthFilter) {        	
//        	//###START### - FOR BROKEN DATA
//        	if (checkVertexExist.contains(vertex)) {
//        		//existingText += vertex.id() + ", ";
//        		//System.out.println("Exist already" + (Integer) vertex.value("id"));
//        	} else {
//        		//System.out.println("NEW: " + (Integer) vertex.value("id"));
//        		//newText += vertex.id() + ", ";
//        		checkVertexExist.add(vertex);
//        		alignmentBlockAssociation.add(vertex, alignmentBlock);
//        		sequence.add(vertex);
//        		addAdditionalVertexData(vertex);
//        	}
//        	//###END### - FOR BROKEN DATA
    		
        	alignmentBlockAssociation.add(vertex, alignmentBlock);
    		sequence.add(vertex);
    		addAdditionalVertexData(vertex);
        }
        
        if (sequence.size() % 20000 == 0) {
        	System.out.println(sequence.size());
        }
    }

  //Klick on Paint Graph Button --> to query the compareable Chromosome Sequences from the GraphDB
    //7. Step
    public void n4JPaintGraph(List<String> selectedComparableSubStructures, boolean join, int thickness, int space) {
    	long timeStart = System.currentTimeMillis();
    	System.out.println("Draw Graph");
    	n4JSequenceVerticesData = new N4JSequenceVerticesData();
    	Pair pair = new ImmutablePair<Node, List<Node>>(n4JGuideSubStructure, n4JGuidePath);
    	String genomeName;
    	String subStructureName;
    	if (!(n4JGuideSubStructure.get("name").toString().replaceAll("\"", "") == "NULL")) {
    		genomeName = n4JGuideGenome.get("name").toString().replaceAll("\"", "");
    		subStructureName = n4JGuideSubStructure.get("name").toString().replaceAll("\"", ""); 
    	} else {
    		subStructureName = superGenomeIdentifiere + n4JGuideSubStructure.get("id").toString().replaceAll("\"", "");
    		genomeName = superGenomeIdentifiere;
    	}
//    	System.out.println(pair + "; "  + subStructureName + "; "  + genomeName);
//    	String text2 = "neu: " + n4JGuideSubStructure.get("name") + ": ";
//    	for (Node n : n4JGuidePath) {
//    		text2 = text2 + n.get("id") + ", ";
//    	}
//    	System.out.println(text2);
//    	System.out.println(n4JGuidePath.hashCode());
        n4JSequenceVerticesData.add(pair, subStructureName, genomeName);
        
        for (String comparableSubStructure : selectedComparableSubStructures) {
        	Node otherChromosomeVertex = n4JCompareableSubStructuresVertexAssociation.get(comparableSubStructure);
        	n4JSequence = null;
        	
        	if (comparableSubStructure.startsWith(superGenomeIdentifiere)) {
                //TO-DO mehrere Super Genome Vertices einbauen!!!!
        		//System.out.println("super genome");
            	Map<String,Object> params = new HashMap<>();
            	params.put("guidePath", n4JGuidePath.stream().map(vertex -> vertex.get("id").asInt()).collect(Collectors.toList()));
            	
            	try (Session session = getSession()) {
            	    org.neo4j.driver.Result result = session.run("MATCH (v:Vertex)<-[next:next]-() WHERE v.id IN $guidePath RETURN max(next.pos) as max, min(next.pos) as min", params);
            	    //System.out.println(params.get("guidePath"));
            	    Record record = result.next();
            	    params.put("minPos", record.get("min").asInt());
            	    params.put("maxPos", record.get("max").asInt());
            	}
            	
            	//System.out.println("Limits: " + params.get("minPos") + " " + params.get("maxPos"));
            	
            	try (Session session = getSession()) {
            	    org.neo4j.driver.Result result = session.run("MATCH (block:Block)<-[:containsBlock]-(v:Vertex)<-[next:next]-() WHERE next.pos >= $minPos AND next.pos <= $maxPos RETURN v, block ORDER BY next.pos", params);
            	    n4JStream = result.stream();
            	    n4JStream.forEachOrdered(this::n4JFillSequence);
            	}
            	
        	} else {
            	//System.out.println("normal substructure");
            	Map<String,Object> params = new HashMap<>();
            	params.put("guidePath", n4JGuidePath.stream().map(vertex -> vertex.get("id").asInt()).collect(Collectors.toList()));
            	params.put("otherChromosomeVertexId", otherChromosomeVertex.get("id"));
            	
            	//System.out.println(otherChromosomeVertex.get("name"));
            	
            	try (Session session = getSession()) {
            	    org.neo4j.driver.Result result = session.run("MATCH (v:Vertex)-[:containsBlock]->(:Block)-[:hasSeq]->(seq:Sequence)-[:isOn]->(:Chromosome {id:$otherChromosomeVertexId}) WHERE v.id IN $guidePath OPTIONAL MATCH (:Chromosome {id:$otherChromosomeVertexId})<-[:val]-(e:Edge)-[:graphedge]->(v:Vertex) WITH CASE WHEN e.position IS NULL THEN -1 ELSE e.position END AS pos RETURN max(pos) as max, min(pos) as min", params);
            	    Record record = result.next();
            	    params.put("minPos", record.get("min").asInt());
            	    params.put("maxPos", record.get("max").asInt());
            	}
            	
            	//System.out.println("Limits: " + params.get("minPos") + " " + params.get("maxPos"));
            	
            	//String text = "";
            	//Special Case, to get the first Vertex of this SubStructure without ingoing "graphedge"-Edge
            	if ((int) params.get("minPos") == -1) {
            		//System.out.println("Special Case");
            		try (Session session = getSession()) {
                	    org.neo4j.driver.Result result = session.run("MATCH (c:Chromosome {id:$otherChromosomeVertexId})<-[:val]-(e:Edge {position:0})<-[:graphedge]-(v:Vertex)-[:containsBlock]->(block:Block) RETURN v, block ", params);
                	    n4JStream = result.stream();
                	    n4JStream.forEachOrdered(this::n4JFillSequence);
                	}
            	}
            	
            	try (Session session = getSession()) {
            	    org.neo4j.driver.Result result = session.run("MATCH (c:Chromosome {id:$otherChromosomeVertexId})<-[:val]-(e:Edge)-[:graphedge]->(v:Vertex)-[:containsBlock]->(block:Block) WHERE e.position >= $minPos AND e.position <= $maxPos RETURN v, block ORDER BY e.position", params);
            	    n4JStream = result.stream();
            	    n4JStream.forEachOrdered(this::n4JFillSequence);
            	}
            	
        	}
        	
        	if (!n4JSequence.isEmpty()) {
        		n4JAddAdditionalVertexData(n4JSequence);
                pair = new ImmutablePair<Node, List<Node>>(otherChromosomeVertex, n4JSequence);
                n4JSequenceVerticesData.add(pair, comparableSubStructure);
            }
        }
        
//        for (Pair<Node, List<Node>> p : n4JSequenceVerticesData.getSequenceVerticesData()) {
//        	String text = p.getKey().get("name") + ": ";
//        	for (Node n : p.getValue()) {
//        		text = text + n.get("id") + ", ";
//        	}
//        	System.out.println(text);
//        }
        
        long timeEnd = System.currentTimeMillis();
        long timeDif = timeEnd - timeStart;
        System.out.println("Create seq. time: " + timeDif);
        
      //starting the Sugiyama Algorithm
        configuration = new Configuration();
        configuration.setN4JSequenceVerticesData(n4JSequenceVerticesData);
        configuration.setN4JAlignmentBlockAssociation(n4JAlignmentBlockAssociation);
        configuration.setIsJoinEnabled(join);
        configuration.setDrawingThicknessFactor(thickness);
        configuration.setSpaceFactor(space);
        configuration.setN4JAdditionalVertexData(n4JAdditionalVertexData);
        startSugiyama();
    }
    
    public void n4JPaintGraphWithReorderWithUpdate(List<String> newOrder) {
    	int newGSIndex = n4JSequenceVerticesData.getIndexForComposedName(newOrder.get(0));
    	System.out.println("New GS Index: " + newGSIndex);
    	
    	Node n4JGuideSubStructureOld = n4JGuideSubStructure;
    	Node n4JGuideGenomeOld = n4JGuideGenome;
    	List<Node> n4JGuidePathOld = n4JGuidePath;
    	HashMap<String, Node> n4JCompareableSubStructuresVertexAssociationOld = n4JCompareableSubStructuresVertexAssociation;
    	HashMap<String, HashMap<String, Long>> compareableSubStructuresVertexAssociationNewOld = compareableSubStructuresVertexAssociationNew;
    	
    	//n4JCompareableSubStructuresVertexAssociation.put(n4JSequenceVerticesData.getGSComposedName(), n4JGuideSubStructure);
    	//compareableSubStructuresVertexAssociationNew.put(n4JSequenceVerticesData.getGSComposedName(), new HashMap<>());
    	
    	Pair<Node, List<Node>> newGSPair = n4JSequenceVerticesData.getSequenceVerticesDataEntry(newGSIndex);
    	n4JGuideSubStructure = newGSPair.getKey();
    	for (String genomeName : n4JGenomeVertexAssociation.keySet()) {
    		if (genomeName.startsWith(n4JSequenceVerticesData.getGenomeNameEntry(newGSIndex))) {
    			n4JGuideGenome = n4JGenomeVertexAssociation.get(genomeName);
    		}
    	}
//    	List<Node> newGSPath = new ArrayList<>();
//    	for (Node n : newGSPair.getValue()) {
//    		newGSPath.add(n);
//    	}
//    	n4JGuidePath = newGSPath;
    	n4JTemp_guidePath = newGSPair.getValue();
    	
    	String text = "neu: " + n4JGuideSubStructure.get("name") + ": ";
    	for (Node n : n4JGuidePath) {
    		text = text + n.get("id") + ", ";
    	}
    	System.out.println(text);
    	System.out.println(n4JGuidePath.hashCode());
    	
    	List<String> newCSList = new ArrayList<>();
    	HashMap<String, HashMap<String,Long>> newPossibleCS = n4JGetComparableChromosomesAndSpecies(this.lengthFilter);
    	
    	for (int i = 1; i < newOrder.size(); i++) {
    		String composedName = newOrder.get(i);
    		String[] split = composedName.split(" ");
    		String subStructureName = split[0];
    		String genomeName = split[1].substring(1, split[1].length() - 1);
    		if (newPossibleCS.containsKey(genomeName)) {
    			for (String possibleSubStructures : newPossibleCS.get(genomeName).keySet()) {
    				if (subStructureName.equals(possibleSubStructures.split(" ")[0])) {
    					newCSList.add(possibleSubStructures + " (" + genomeName + ")");
    					System.out.println("New CS: " + possibleSubStructures + " (" + genomeName + ")");
    					System.out.println(n4JCompareableSubStructuresVertexAssociation.get(possibleSubStructures + " (" + genomeName + ")").get("name"));
    				}
    			}
    		}
    	}    	
    	
    	n4JPaintGraph(newCSList, configuration.getIsJoinEnabled(), (int) configuration.getDrawingThicknessFactor(), configuration.getSpaceFactor());
    	
    	n4JGuideSubStructure = n4JGuideSubStructureOld;
    	n4JGuidePath = n4JGuidePathOld;
    	n4JGuideGenome = n4JGuideGenomeOld;
    	n4JCompareableSubStructuresVertexAssociation = n4JCompareableSubStructuresVertexAssociationOld;
    	compareableSubStructuresVertexAssociationNew = compareableSubStructuresVertexAssociationNewOld;
    }
    
    public void n4JPaintGraphWithReorder(List<String> newOrder) {
    	n4JSequenceVerticesData.reOrderSequences(newOrder);
    	configuration.setN4JSequenceVerticesData(n4JSequenceVerticesData);
    	startSugiyama();
    }
    
    //Klick on Paint Graph Button --> to query the compareable Chromosome Sequences from the GraphDB
    //7. Step
    public void paintGraph(List<String> selectedComparableSubStructures, boolean join, int thickness, int space) {
    	long timeStart = System.currentTimeMillis();
    	System.out.println("Paint");
        sequenceVerticesData = new SequenceVerticesData();
        Pair pair = new ImmutablePair<Vertex, List<Vertex>>(guideSubStructure, guidePath);
        String genomeName = guideGenome.value("name").toString();
        String subStructureName = guideSubStructure.value("name").toString(); 
        sequenceVerticesData.add(pair, subStructureName, genomeName);
        
        int boundarieInteval = 500;

//        Chromosome guideChromosomePath = new Chromosome(guideChromosome, guidePath, freeColor.getFreeColor());
//        sequenceVerticesData.add(guideChromosomePath);
        for (String comparableSubStructure : selectedComparableSubStructures) {
            GraphTraversal<Vertex, Map<String, Object>> traversalSequencesVertices = null;
            ResultSet results = null;
            Vertex otherChromosomeVertex = compareableSubStructuresVertexAssociation.get(comparableSubStructure);
            
            sequence = null;
            //newText = "";
            //existingText = "";
            
            /*String text = "GUIDE: [";
            for (Vertex v : guidePath) {
            	text = text + v.id() + ", ";
            }
            System.out.println(text);*/
            
            if (comparableSubStructure.startsWith(superGenomeIdentifiere)) {
                //TO-DO mehrere Super Genome Vertices einbauen!!!!
            	System.out.println("super genome");
            	Map<String,Object> params = new HashMap<>();
            	params.put("guidePath", guidePath);
            	
//            	results = client.submit("g.V(guidePath).project(\n"
//        				+ "                    \"super_g_pos\", \"guide_vertices\"\n"
//        				+ "                ).by(\n"
//        				+ "                    __.inE(\"next\").values(\"pos\")\n"
//        				+ "                ).by(\n"
//        				+ "                    __.identity()\n"
//        				+ "                ).fold().sideEffect(\n"
//        				+ "                    __.unfold().select(\"super_g_pos\").min().store(\"min_pos\")\n"
//        				+ "                ).sideEffect(\n"
//        				+ "                    __.unfold().select(\"super_g_pos\").max().store(\"max_pos\")\n"
//        				+ "                ).as(\"startset\").unfold().where(\n"
//        				+ "                    \"super_g_pos\", P.within(\"max_pos\")\n"
//        				+ "                ).as(\"end_vertex\").select(\"startset\").unfold().where(\n"
//        				+ "                    \"super_g_pos\", P.within(\"min_pos\")\n"
//        				+ "                ).as(\"start_vertex\").select(\n"
//        				+ "                    \"start_vertex\", \"end_vertex\"\n"
//        				+ "                )", params);
//            	
//            	Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) results.one().getObject();
//            	
//            	int intervals = ((int) map.get("end_vertex").get("super_g_pos") - (int) map.get("start_vertex").get("super_g_pos")) / boundarieInteval;
//            	int perfectMatchInLastInterval = ((int) map.get("end_vertex").get("super_g_pos") - (int) map.get("start_vertex").get("super_g_pos")) % boundarieInteval;
//            	
//            	System.out.println(intervals + ", " + perfectMatchInLastInterval);
            	
//            	List<Integer> boundaries = new ArrayList<>();
//            	boundaries.add((int) map.get("start_vertex").get("super_g_pos"));
//            	boundaries.add((int) map.get("end_vertex").get("super_g_pos"));
//            	
//            	System.out.println(boundaries);
//            	
//            	if (perfectMatchInLastInterval == 0) {
//            		intervals = intervals - 1;
//            	}
//            	
//            	for (int i = 1; i <= intervals; i++) {
//            		int tempBoundarie = boundaries.get(0) + (i * boundarieInteval);
//            		boundaries.add(boundaries.size() - 1, tempBoundarie);
//            	}
//            	
//            	for (int i = 0; i < boundaries.size() - 1; i++) {
//            		
//            		if (i > 0) {
//            			boundaries.set(i, boundaries.get(i) + 1);
//            		}
//            		
//            		params = new HashMap<>();
//            		params.put("guidePath", guidePath);
//            		params.put("start_bound", boundaries.get(i));
//            		params.put("end_bound", boundaries.get(i + 1));
//            		            		
//            		Vertex startVertex = null;
//            		Vertex endVertex = null;
//            		
//            		System.out.println(params.get("start_bound") + ", " + params.get("end_bound"));
//            		
//            		startVertex = client.submit("g.V().hasLabel(\"Vertex\").where(__.inE(\"next\").has(\"pos\", start_bound))", params).one().getVertex();
//            		endVertex = client.submit("g.V().hasLabel(\"Vertex\").where(__.inE(\"next\").has(\"pos\", end_bound))", params).one().getVertex();
//            		
//            	
//            	}
            	
//            	results = client.submit("g.V(guidePath).project(\n"
//        				+ "                    \"super_g_pos\", \"guide_vertices\"\n"
//        				+ "                ).by(\n"
//        				+ "                    __.inE(\"next\").values(\"pos\")\n"
//        				+ "                ).by(\n"
//        				+ "                    __.identity()\n"
//        				+ "                ).fold().sideEffect(\n"
//        				+ "                    __.unfold().select(\"super_g_pos\").min().store(\"min_pos\")\n"
//        				+ "                ).sideEffect(\n"
//        				+ "                    __.unfold().select(\"super_g_pos\").max().store(\"max_pos\")\n"
//        				+ "                ).as(\"startset\").unfold().where(\n"
//        				+ "                    \"super_g_pos\", P.within(\"max_pos\")\n"
//        				+ "                ).select(\"guide_vertices\").as(\"end_vertex\").select(\"startset\").unfold().where(\n"
//        				+ "                    \"super_g_pos\", P.within(\"min_pos\")\n"
//        				+ "                ).select(\"guide_vertices\").as(\"start_vertex\").<Vertex>select(\"start_vertex\").emit().until(\n"
//        				+ "                    __.where(P.eq(\"end_vertex\"))\n"
//        				+ "                ).repeat(\n"
//        				+ "                    __.out(\"next\")\n"
//        				+ "                ).project(\"vertex\", \"block\"\n"
//        				+ "                ).by(__.identity()\n"
//        				+ "                ).by(__.out(\"containsBlock\").dedup()).count()", params);
//            	
//            	System.out.println("Vertex Count: " + results.one().getInt());
            	
        		results = client.submit("g.V(guidePath).project(\n"
        				+ "                    \"super_g_pos\", \"guide_vertices\"\n"
        				+ "                ).by(\n"
        				+ "                    __.inE(\"next\").values(\"pos\")\n"
        				+ "                ).by(\n"
        				+ "                    __.identity()\n"
        				+ "                ).fold().sideEffect(\n"
        				+ "                    __.unfold().select(\"super_g_pos\").min().store(\"min_pos\")\n"
        				+ "                ).sideEffect(\n"
        				+ "                    __.unfold().select(\"super_g_pos\").max().store(\"max_pos\")\n"
        				+ "                ).as(\"startset\").unfold().where(\n"
        				+ "                    \"super_g_pos\", P.within(\"max_pos\")\n"
        				+ "                ).select(\"guide_vertices\").as(\"end_vertex\").select(\"startset\").unfold().where(\n"
        				+ "                    \"super_g_pos\", P.within(\"min_pos\")\n"
        				+ "                ).select(\"guide_vertices\").as(\"start_vertex\").<Vertex>select(\"start_vertex\").emit().until(\n"
        				+ "                    __.where(P.eq(\"end_vertex\"))\n"
        				+ "                ).repeat(\n"
        				+ "                    __.out(\"next\")\n"
        				+ "                ).project(\"vertex\", \"block\"\n"
        				+ "                ).by(__.identity()\n"
        				+ "                ).by(__.out(\"containsBlock\").dedup())", params);
            	
        		stream = results.stream(); 
        		stream.forEachOrdered(this::fillSequence);
        		
        		//System.out.println(results.one());
        		
//                traversalSequencesVertices = graphTraversal.V(guidePath).project(
//                    "super_g_pos", "guide_vertices"
//                ).by(
//                    __.inE("next").values("pos")
//                ).by(
//                    __.identity()
//                ).fold().sideEffect(
//                    __.unfold().select("super_g_pos").min().store("min_pos")
//                ).sideEffect(
//                    __.unfold().select("super_g_pos").max().store("max_pos")
//                ).as("startset").unfold().where(
//                    "super_g_pos", P.within("max_pos")
//                ).select("guide_vertices").as("end_vertex").select("startset").unfold().where(
//                    "super_g_pos", P.within("min_pos")
//                ).select("guide_vertices").as("start_vertex").<Vertex>select("start_vertex").emit().until(
//                    __.where(P.eq("end_vertex"))
//                ).repeat(
//                    __.out("next")
//                ).project("vertex", "block"
//                ).by(__.identity()
//                ).by(__.out("containsBlock").dedup());
            } else {
            	System.out.println("normal substructure");
            	Map<String,Object> params = new HashMap<>();
            	params.put("guidePath", guidePath);
            	params.put("otherChromosomeVertex", otherChromosomeVertex);
            	
            	System.out.println(otherChromosomeVertex.values("name"));
            	
            	results = client.submit("g.V(guidePath).where(\n"
            			+ "                    __.out(\"containsBlock\").dedup().out(\"hasSeq\").out(\"isOn\").is(otherChromosomeVertex)\n"
            			+ "                ).choose(\n"
            			+ "                    __.in(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)),\n"
            			+ "                    __.project(\"genome_pos\", \"guide_vertices\").by(__.in(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)).values(\"position\")).by(__.identity()),\n"
            			+ "                    __.project(\"genome_pos\", \"guide_vertices\").by(__.constant(-1)).by(__.identity())\n"
            			+ "                ).fold().sideEffect(\n"
            			+ "                    __.unfold().select(\"genome_pos\").min().store(\"min_pos\")\n"
            			+ "                ).sideEffect(\n"
            			+ "                    __.unfold().select(\"genome_pos\").max().store(\"max_pos\")\n"
            			+ "                ).as(\"startset\").unfold().where(\"genome_pos\", P.within(\"max_pos\")).as(\"end_vertex\"\n"
            			+ "                ).select(\"startset\").unfold().where(\"genome_pos\", P.within(\"min_pos\")).as(\"start_vertex\"\n"
            			+ "                ).select(\"start_vertex\", \"end_vertex\")", params);
            	
            	Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) results.one().getObject();
            	
            	int intervals = ((int) map.get("end_vertex").get("genome_pos") - (int) map.get("start_vertex").get("genome_pos")) / boundarieInteval;
            	int perfectMatchInLastInterval = ((int) map.get("end_vertex").get("genome_pos") - (int) map.get("start_vertex").get("genome_pos")) % boundarieInteval;
            	
            	//System.out.println(intervals + ", " + perfectMatchInLastInterval);
            	
            	List<Integer> boundaries = new ArrayList<>();
            	boundaries.add((int) map.get("start_vertex").get("genome_pos"));
            	boundaries.add((int) map.get("end_vertex").get("genome_pos"));
            	//System.out.println(boundaries.get(0) + "--" + boundaries.get(1));
            	
            	System.out.println(boundaries);
            	
            	if (perfectMatchInLastInterval == 0) {
            		intervals = intervals - 1;
            	}
            	
            	for (int i = 1; i <= intervals; i++) {
            		int tempBoundarie = boundaries.get(0) + (i * boundarieInteval);
            		boundaries.add(boundaries.size() - 1, tempBoundarie);
            	}
            	
            	for (int i = 0; i < boundaries.size() - 1; i++) {
            		
            		if (i > 0) {
            			boundaries.set(i, boundaries.get(i) + 1);
            		}
            		
            		params = new HashMap<>();
            		params.put("otherChromosomeVertex", otherChromosomeVertex);
            		params.put("start_bound", boundaries.get(i));
            		params.put("end_bound", boundaries.get(i + 1));
            		            		
            		Vertex startVertex = null;
            		Vertex endVertex = null;
            		
            		if ((int) params.get("start_bound") != -1) {
            			startVertex = client.submit("g.V(otherChromosomeVertex).in(\"val\").where(__.has(\"position\", start_bound)).out(\"graphedge\")", params).one().getVertex();
            		} else if ((int) params.get("start_bound") == (int) params.get("end_bound")) {
            			startVertex = (Vertex) map.get("start_vertex").get("guide_vertices");
            			endVertex = startVertex;
            		} else {
            			startVertex = client.submit("g.V(otherChromosomeVertex).in(\"val\").where(__.has(\"position\", 0)).in(\"graphedge\")", params).one().getVertex();
            			//params.put("start_bound", 0);
            		}
            		
            		if (endVertex == null) {
            			endVertex = client.submit("g.V(otherChromosomeVertex).in(\"val\").where(__.has(\"position\", end_bound)).out(\"graphedge\")", params).one().getVertex();
            		}
            		
            		if ((int) params.get("end_bound") == -1) {
            			params.replace("end_bound", 0);
            		}
            		params.replace("end_bound", ((int) params.get("end_bound")) + 1);
            			
            		params.put("startVertex", startVertex);
            		params.put("negStart_bound", - (int) params.get("start_bound"));
            		params.put("endVertex", endVertex);
            		
            		//System.out.println(params.get("start_bound") + " - " + params.get("end_bound") + "; " + params.get("negStart_bound"));
            		
            		//String breakthrough = "g.withSack(0).withSideEffect(\"p\", -300).V(39995053).emit().until(__.hasId(39995071)).repeat(__.out(\"graphedge\").where(__.out(\"val\").hasId(2)).where(__.sack(sum).by(\"position\").sack(sum).by(constant(-1)).sack().math(\"_ + p\").is(0)).out(\"graphedge\").sack(sum).by(constant(-1)))\n";
            		
            		results = client.submit("g.withSack(0).withSideEffect(\"p\", negStart_bound).V(startVertex).emit().until(\n"
                			+ "                    __.where(is(endVertex))\n"
                			+ "                ).repeat(\n"
                								//###START### - DAS IST NUR WICHTIG FÜR KAPUTTE DATEN MIT ZYKLEN!!!!
                			//+ "                    __.out(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)).where(__.sack(sum).by(\"position\").sack(sum).by(constant(-1)).sack().math(\"_ + p\").is(0)).out(\"graphedge\").sack(sum).by(constant(-1))\n"
                								//###END### - DAS IST NUR WICHTIG FÜR KAPUTTE DATEN MIT ZYKLEN!!!!
                			+ "                    __.out(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)).where(__.values(\"position\").is(inside(start_bound, end_bound))).out(\"graphedge\")\n"
                			//+ "                    __.out(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)).out(\"graphedge\")\n"
                			+ "                ).project(\"vertex\", \"block\"\n"
                			+ "                ).by(__.identity()\n"
                			+ "                ).by(__.out(\"containsBlock\").dedup())", params);
            		
            		stream = results.stream();
            		stream.forEachOrdered(this::fillSequence);
            	}
            	
            	//System.out.println("Exists: " + existingText);
            	//System.out.println("New: " + newText);
            	
            	//System.out.println("---");
            	
//            	params = new HashMap<>();
//            	params.put("guidePath", guidePath);
//            	params.put("otherChromosomeVertex", otherChromosomeVertex);
//            	
//            	results = client.submit("g.V(guidePath).where(\n"
//            			+ "                    __.out(\"containsBlock\").dedup().out(\"hasSeq\").out(\"isOn\").is(otherChromosomeVertex)\n"
//            			+ "                ).choose(\n"
//            			+ "                    __.in(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)),\n"
//            			+ "                    __.project(\"genome_pos\", \"guide_vertices\").by(__.in(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)).values(\"position\")).by(__.identity()),\n"
//            			+ "                    __.project(\"genome_pos\", \"guide_vertices\").by(__.constant(-1)).by(__.identity())\n"
//            			+ "                ).fold().sideEffect(\n"
//            			+ "                    __.unfold().select(\"genome_pos\").min().store(\"min_pos\")\n"
//            			+ "                ).sideEffect(\n"
//            			+ "                    __.unfold().select(\"genome_pos\").max().store(\"max_pos\")\n"
//            			+ "                ).as(\"startset\").unfold().where(\"genome_pos\", P.within(\"max_pos\")).select(\"guide_vertices\").as(\"end_vertex\"\n"
//            			+ "                ).select(\"startset\").unfold().where(\"genome_pos\", P.within(\"min_pos\")).select(\"guide_vertices\").as(\"start_vertex\"\n"
//            			+ "                ).<Vertex>select(\"start_vertex\").emit().until(\n"
//            			+ "                    __.where(P.eq(\"end_vertex\"))\n"
//            			+ "                ).repeat(\n"
//            			+ "                    __.out(\"graphedge\").where(__.out(\"val\").is(otherChromosomeVertex)).out(\"graphedge\")\n"
//            			+ "                ).project(\"vertex\", \"block\"\n"
//            			+ "                ).by(__.identity()\n"
//            			+ "                ).by(__.out(\"containsBlock\").dedup())", params);
//            	
//            	results.stream().forEachOrdered(this::fillSequence);
            	
//                traversalSequencesVertices = graphTraversal.V(guidePath).where(
//                    __.out("containsBlock").dedup().out("hasSeq").out("isOn").is(otherChromosomeVertex)
//                ).choose(
//                    __.in("graphedge").where(__.out("val").is(otherChromosomeVertex)),
//                    __.project("genome_pos", "guide_vertices").by(__.in("graphedge").where(__.out("val").is(otherChromosomeVertex)).values("position")).by(__.identity()),
//                    __.project("genome_pos", "guide_vertices").by(__.constant(-1)).by(__.identity())
//                ).fold().sideEffect(
//                    __.unfold().select("genome_pos").min().store("min_pos")
//                ).sideEffect(
//                    __.unfold().select("genome_pos").max().store("max_pos")
//                ).as("startset").unfold().where("genome_pos", P.within("max_pos")).select("guide_vertices").as("end_vertex"
//                ).select("startset").unfold().where("genome_pos", P.within("min_pos")).select("guide_vertices").as("start_vertex"
//                ).<Vertex>select("start_vertex").emit().until(
//                    __.where(P.eq("end_vertex"))
//                ).repeat(
//                    __.out("graphedge").where(__.out("val").is(otherChromosomeVertex)).out("graphedge")
//                ).project("vertex", "block"
//                ).by(__.identity()
//                ).by(__.out("containsBlock").dedup());
            }            
            
//            if (sequenceIterator.hasNext()) {
//                sequence = new ArrayList<>();
//            }
            
            
//            String text = "[";
//            String text2 = "[";
//            while (traversalSequencesVertices.hasNext()) {
//                Map<String, Object> mapEntry = traversalSequencesVertices.next();
//                Vertex vertex = (Vertex) mapEntry.get("vertex");
//                Vertex alignmentBlock = (Vertex) mapEntry.get("block");
//                              
//                text2 = text2 + vertex.id() + ", ";
//                
//                if ((Integer) alignmentBlock.value("length") >= lengthFilter) {
//                	
//                    alignmentBlockAssociation.add(vertex, alignmentBlock);
//                    sequence.add(vertex);
//                    addAdditionalVertexData(vertex);
//                    text = text + vertex.id() + ", ";
//                }
//            }
//            text = text.substring(0, text.length() - 2);
//        	text = text + "]";
//        	text2 = text2.substring(0, text2.length() - 2);
//        	text2 = text2 + "]";
//        	System.out.println(text2);
//        	System.out.println(text);
        	            
            if (!sequence.isEmpty()) {
                pair = new ImmutablePair<Vertex, List<Vertex>>(otherChromosomeVertex, sequence);
                sequenceVerticesData.add(pair, comparableSubStructure);
            }
        }

        long timeEnd = System.currentTimeMillis();
        long timeDif = timeEnd - timeStart;
        System.out.println("Create seq. time: " + timeDif);
        
//        chromosomesToDraw = new HashSet<>();
//        CheckBoxTreeItem<String> newRoot = new CheckBoxTreeItem("All");
//        CheckBoxTreeItem newItem = new CheckBoxTreeItem(guideStructure.value("name") + " (Guide Sequence)");
//        newRoot.getChildren().add(newItem);
//        isChecked((CheckBoxTreeItem<String>) treeViewOtherChromosomesAndSpecies.getRoot(), newRoot);
//        treeViewDrawedChromosomes.setRoot(newRoot);
//        treeViewDrawedChromosomes.setDisable(false);
//        newRoot.setSelected(false);

        //EventHandler --> TreeView something is ticked --> change Drawing
//        treeViewDrawedChromosomes.getRoot().addEventHandler(
//            CheckBoxTreeItem.checkBoxSelectionChangedEvent(),
//            new EventHandler<CheckBoxTreeItem.TreeModificationEvent<String>>() {
//
//            @Override
//            public void handle(CheckBoxTreeItem.TreeModificationEvent<String> evt) {
//
//                CheckBoxTreeItem<String> item = evt.getTreeItem();
//                if (!item.equals(newRoot)) {
//                    if (evt.wasIndeterminateChanged()) {
//                        if (item.isLeaf()) {
////                            System.out.println("indeterminate");
//                            if (item.isIndeterminate()) {
//                                String chromosomeText = item.getValue().split("\\(")[0];
//                                chromosomeText = chromosomeText.substring(0, chromosomeText.length() - 1);
////                                System.out.println("remove:" + chromosomeText);
//                                Vertex chromosomeVertex;
//                                if (chromosomeText.equals(guideStructure.value("name").toString())) {
//                                    chromosomeVertex = guideStructure;
//                                } else {
//                                    chromosomeVertex = compareableChromosomesVertexAssociation.get(chromosomeText);
//                                }
//                                chromosomesToDraw.remove(chromosomeVertex);
//                            } else if (evt.getTreeItem().isSelected()) {
//                                String chromosomeText = item.getValue().split("\\(")[0];
//                                chromosomeText = chromosomeText.substring(0, chromosomeText.length() - 1);
////                                System.out.println("add:" + chromosomeText);
//                                Vertex chromosomeVertex;
//                                if (chromosomeText.equals(guideStructure.value("name").toString())) {
//                                    chromosomeVertex = guideStructure;
//                                } else {
//                                    chromosomeVertex = compareableChromosomesVertexAssociation.get(chromosomeText);
//                                }
//                                chromosomesToDraw.add(chromosomeVertex);
//                            }
//                        }
//                    } else if (evt.wasSelectionChanged()) {
//                        if (item.isLeaf()) {
////                            System.out.println("determinate");
//                            if (item.isSelected()) {
//                                String chromosomeText = item.getValue().split("\\(")[0];
//                                chromosomeText = chromosomeText.substring(0, chromosomeText.length() - 1);
////                                System.out.println("add:" + chromosomeText);
//                                Vertex chromosomeVertex;
//                                if (chromosomeText.equals(guideStructure.value("name").toString())) {
//                                    chromosomeVertex = guideStructure;
//                                } else {
//                                    chromosomeVertex = compareableChromosomesVertexAssociation.get(chromosomeText);
//                                }
//                                chromosomesToDraw.add(chromosomeVertex);
//                            } else {
//                                String chromosomeText = item.getValue().split("\\(")[0];
//                                chromosomeText = chromosomeText.substring(0, chromosomeText.length() - 1);
////                                System.out.println("remove:" + chromosomeText);
//                                Vertex chromosomeVertex;
//                                if (chromosomeText.equals(guideStructure.value("name").toString())) {
//                                    chromosomeVertex = guideStructure;
//                                } else {
//                                    chromosomeVertex = compareableChromosomesVertexAssociation.get(chromosomeText);
//                                }
//                                chromosomesToDraw.remove(chromosomeVertex);
//                            }
//                        }
//                    }
//                }
//
//                parent.reDrawSugiyamaFramework(chromosomesToDraw);
//            }
//        });

        //starting the Sugiyama Algorithm
        configuration = new Configuration();
        //Test!!!
//        configuration.setCsvText(csvText);
//        //Configuration in CSV
//        //GS-Species;GS-Structure;GS-Start-Pos.;GS-End-Pos.;Length-Filter;Order-Comparative-Sequences;Join
//        StringBuffer compSeqOrder = new StringBuffer();
//        for (Object entryObject : tableOtherChromosomeOrder.getItems()) {
//            TableViewEntry entry = (TableViewEntry) entryObject;
//            compSeqOrder.append(entry.getChromosome() + "(" + entry.getSpecies() + "),");
//        }
//        compSeqOrder.setLength(compSeqOrder.length() - 1); //delete last Comma
//        csvText.append(guideSpecies.value("name").toString() + ";" + guideStructure.value("name").toString() + ";" + txtFldStart.getText() + ";" + txtFldEnd.getText() + ";" + txtFldMinLength.getText() + ";" + compSeqOrder + ";" + joinCheckBox.isSelected() + ";");
        //Test!!!
        configuration.setSequenceVerticesData(sequenceVerticesData);
        configuration.setAlignmentBlockAssociation(alignmentBlockAssociation);
        configuration.setIsJoinEnabled(join);
        configuration.setDrawingThicknessFactor(thickness);
        configuration.setSpaceFactor(space);
        configuration.setAdditionalVertexData(additionalVertexData);
        startSugiyama();
    }
    
	private void startSugiyama() {//, Set<Vertex> subStructuresToDraw) {
		long timeStart = System.currentTimeMillis();

		sugiyamaProjection = new GraphProjectionSugiyama(configuration);
		sugiyamaProjection.computeLayout(configuration);
		//drawSugiyamaFramework(sugiyamaProjection, subStructuresToDraw);

		long timeEnd = System.currentTimeMillis();
		// TEST!!!
//            StringBuffer sb = sugiyamaProjection.getCSVLine();
//            sb.append((timeEnd - timeStart) + "\n");
		// TEST!!!
	}
	
//
//    @FXML
//    private void snapshot() {
//        parent.snapshot();
//    }
//
//    private void isChecked(CheckBoxTreeItem<String> item, TreeItem<String> newRoot) {
//        if (item.isLeaf() && item.isSelected()) {
//            CheckBoxTreeItem newItem = new CheckBoxTreeItem(item.getValue() + " (" + item.getParent().getValue() + ")");
//            newRoot.getChildren().add(newItem);
////            chromosomesToDraw.add(compareableChromosomesVertexAssociation.get(item.getValue()));
//        } else {
//            for (TreeItem<String> child : item.getChildren()) {
//                isChecked((CheckBoxTreeItem<String>) child, newRoot);
//            }
//        }
//    }

    // Commit DB and close Connection
    public void stopDB() {
        System.out.println("Close connection");
        if (graphDB != null) {
            try {
                graphDB.tx().commit();
            } catch (UnsupportedOperationException e) {
            }
            try {
                graphDB.close();
            } catch (Exception e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            //initStatus();
        }
        graphDB = null;
    }
    
    public void stopNeo4JDB() {
    	System.out.println("Stop Neo4J Connection");
    	driver.close();
    }
    
    private void addAdditionalVertexData(Vertex currentVertex) {
    	if (!additionalVertexData.containsKey(currentVertex)) {
    		 Map<String,Object> params = new HashMap<>();
	    	params.put("currentVertex", currentVertex.id().toString());
	    	ResultSet results = client.submit("g.V(currentVertex).out(\"containsBlock\").dedup().out(\"hasSeq\").project(\"subStructure\", \"baseSeq\", \"start\", \"length\", \"strand\").by(__.out(\"isOn\").values(\"name\")).by(__.coalesce(__.values(\"seq\"), __.constant(\"\"))).by(__.values(\"start\")).by(__.values(\"length\")).by(__.values(\"strand\")).toList()", params);
	    	HashMap<String, HashMap<String, String>> msa = new HashMap<String, HashMap<String, String>>();
	    	
	    	Iterator<Result> guideSeqIterator =  results.iterator();
    		while(guideSeqIterator.hasNext()) {
    			Result r = guideSeqIterator.next();
    			Map<String, Object> entry = (Map<String, Object>) r.getObject();
    			HashMap<String, String> values = new HashMap<String, String>();
    			values.put("baseSeq", entry.get("baseSeq").toString());
	    		values.put("start", entry.get("start").toString());
	    		values.put("length", entry.get("length").toString());
	    		values.put("strand", entry.get("strand").toString());
	    		msa.put(entry.get("subStructure").toString(), values);
    		}
	    	//List<Map<String, Object>> traversalChromsomes = graphTraversal.V(currentVertex).out("containsBlock").dedup().out("hasSeq").project("subStructure", "baseSeq", "start", "length", "strand").by(__.out("isOn").values("name")).by(__.coalesce(__.values("seq"), __.constant(""))).by(__.values("start")).by(__.values("length")).by(__.values("strand")).toList();
//	    	HashMap<String, HashMap<String, String>> msa = new HashMap<String, HashMap<String, String>>();
//	    	for (Map<String, Object> entry : traversalChromsomes) {
//	    		HashMap<String, String> values = new HashMap<String, String>();
//	    		values.put("baseSeq", entry.get("baseSeq").toString());
//	    		values.put("start", entry.get("start").toString());
//	    		values.put("length", entry.get("length").toString());
//	    		values.put("strand", entry.get("strand").toString());
//	    		msa.put(entry.get("subStructure").toString(), values);
//	    	}
    		
	    	additionalVertexData.put(currentVertex, new AdditionalVertexData(msa));
    	}
    }
    
    private void n4JAddAdditionalVertexData(List<Node> vertexList) {
    	List<Node> newVertexList = new ArrayList<Node>();
    	for (Node currentVertex : vertexList) {
    		if (!n4JAdditionalVertexData.containsKey(currentVertex)) {
    			newVertexList.add(currentVertex);
    		} 
    	}
    	
    	Map<String,Object> params = new HashMap<>();
    	params.put("currentVertexList", newVertexList.stream().map(vertex -> vertex.get("id").asInt()).collect(Collectors.toList()));
    	try (Session session = getSession()) {
    	    org.neo4j.driver.Result result = session.run("MATCH (v:Vertex)-[:containsBlock]->(:Block)-[:hasSeq]->(seq:Sequence)-[:isOn]->(subStructure:Chromosome)<--(species:Species) WHERE v.id IN $currentVertexList RETURN v, collect(subStructure) as subStructure, collect(species) as genome, collect(seq) as seq", params);
    	    
    	    while (result.hasNext()) {
    	    	Record record = result.next();
    	    	org.neo4j.driver.types.Node currentVertex = record.get("v").asNode();
    	    	List<Object> seqList = record.get("seq").asList();
    	    	List<Object> subStructureList = record.get("subStructure").asList();
    	    	List<Object> genomeList = record.get("genome").asList();
    	    	HashMap<String, HashMap<String, String>> msa = new HashMap<String, HashMap<String, String>>();
    	    	for (int i=0; i <= seqList.size() - 1; i++) {
    	    		Node seq = (Node) seqList.get(i);
    	    		Node subStructure = (Node) subStructureList.get(i);
    	    		Node genome = (Node) genomeList.get(i);
    	    		HashMap<String, String> values = new HashMap<String, String>();
	    	    	values.put("baseSeq", seq.get("seq").toString());
	    	    	values.put("start", seq.get("start").toString());
		    		values.put("length", seq.get("length").toString());
		    		values.put("strand", seq.get("strand").toString());
		    		String composedName = subStructure.get("name").toString().replaceAll("\"", "") + " (" + genome.get("name").toString().replaceAll("\"", "") + ")";
		    		msa.put(composedName, values);
    	    	}
    	    	n4JAdditionalVertexData.put(currentVertex, new AdditionalVertexData(msa));
    	    }
    	}
    	
//    	if (!n4JAdditionalVertexData.containsKey(currentVertex)) {
//    		Map<String,Object> params = new HashMap<>();
//    		params.put("currentVertex", currentVertex.get("id").toString());
//	    	HashMap<String, HashMap<String, String>> msa = new HashMap<String, HashMap<String, String>>();
//	    	
//	    	try (Session session = getSession()) {
//	    	    org.neo4j.driver.Result result = session.run("MATCH (:Vertex {id:$currentVertex})-[:containsBlock]->(:Block)-[:hasSeq]->(seq:Sequence)-[:isOn]->(subStructure:Chromosome) RETURN subStructure, seq", params);
//	    	    while (result.hasNext()) {
//	    	    	Record record = result.next();
//	    	    	org.neo4j.driver.types.Node seq = record.get("seq").asNode();
//	    	    	org.neo4j.driver.types.Node subStructure = record.get("subStructure").asNode();
//	    	    	HashMap<String, String> values = new HashMap<String, String>();
//	    	    	values.put("baseSeq", seq.get("seq").toString());
//	    	    	values.put("start", seq.get("start").toString());
//		    		values.put("length", seq.get("length").toString());
//		    		values.put("strand", seq.get("strand").toString());
//		    		msa.put(subStructure.get("name").toString(), values);
//	    	    }
//	    	}
//    		
//	    	n4JAdditionalVertexData.put(currentVertex, new AdditionalVertexData(msa));
//    	}
    }
    
}

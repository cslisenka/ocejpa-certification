<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1423766825119" ID="ID_1340350434" MODIFIED="1423767671915" TEXT="JPA architecture">
<node CREATED="1423997913037" ID="ID_1599488053" MODIFIED="1423997918700" POSITION="right" TEXT="Cascading operations">
<node CREATED="1423997924109" ID="ID_592473570" MODIFIED="1423997942546" TEXT="by default any EM operation applied only to entity"/>
<node CREATED="1423998088796" ID="ID_1231000921" MODIFIED="1423998096731" TEXT="defined by &quot;cascade&quot; attribute">
<node CREATED="1423998097321" ID="ID_265420631" MODIFIED="1423998123945" TEXT="@OneToOne, @OneToMany, @ManyToOne, @ManyToMany"/>
</node>
<node CREATED="1423998142028" ID="ID_363623437" MODIFIED="1423998148397" TEXT="CascadeType enumeration">
<node CREATED="1423998148971" ID="ID_1425983869" MODIFIED="1423998157226" TEXT="PERSIST"/>
<node CREATED="1423998157854" ID="ID_1898508227" MODIFIED="1423998159695" TEXT="REFRESH"/>
<node CREATED="1423998159956" ID="ID_826902296" MODIFIED="1423998163827" TEXT="REMOVE"/>
<node CREATED="1423998164232" ID="ID_1232039626" MODIFIED="1423998165495" TEXT="MERGE"/>
<node CREATED="1423998167677" ID="ID_12238504" MODIFIED="1423998169417" TEXT="DETACH"/>
<node CREATED="1423998179899" ID="ID_589046325" MODIFIED="1423998180730" TEXT="ALL"/>
</node>
<node CREATED="1423998302408" ID="ID_1025800381" MODIFIED="1423998320639" TEXT="we need to set cascading on both sides of relationships separately"/>
<node CREATED="1423998532788" ID="ID_407000131" MODIFIED="1423998545882" TEXT="we can set multiple cascade types for one relationship"/>
</node>
<node CREATED="1423989606937" ID="ID_1010001085" MODIFIED="1423998711940" POSITION="right" TEXT="Transaction management">
<node CREATED="1423989613648" ID="ID_535533542" MODIFIED="1423989625799" TEXT="1. resource-local">
<node CREATED="1423989626480" ID="ID_1268473521" MODIFIED="1423989636348" TEXT="native JDBC transactions"/>
<node CREATED="1423992005890" ID="ID_963679993" MODIFIED="1423992016443" TEXT="em.getTransaction()"/>
<node CREATED="1423992059564" ID="ID_779433755" MODIFIED="1423992069627" TEXT="only 1 transaction can be active">
<node CREATED="1423992070294" ID="ID_1465142268" MODIFIED="1423992087741" TEXT="tx.begin(); tx.begin(); - IllegalStateException"/>
</node>
</node>
<node CREATED="1423989645973" ID="ID_424286497" MODIFIED="1423989828157" TEXT="2. JTA">
<node CREATED="1423989656095" ID="ID_771606719" MODIFIED="1423989661406" TEXT="managed by container"/>
<node CREATED="1423989663616" ID="ID_1591377527" MODIFIED="1423989673761" TEXT="multiple resources"/>
<node CREATED="1423989677413" ID="ID_189317577" MODIFIED="1423989684885" TEXT="distributed XA-transactions"/>
</node>
<node CREATED="1423989816254" ID="ID_757471437" MODIFIED="1423989825532" TEXT="type defined in persistence.xml"/>
<node CREATED="1423989855564" ID="ID_1602292919" MODIFIED="1423989857432" TEXT="tx">
<node CREATED="1423989860591" ID="ID_1677367320" MODIFIED="1423989864338" TEXT="synchronization">
<node CREATED="1423989893214" ID="ID_926271619" MODIFIED="1423989903997" TEXT="persistence context notified when transaction commits"/>
<node CREATED="1423989923933" ID="ID_1816467190" MODIFIED="1423989930595" TEXT="persistence context flushed to DB"/>
</node>
<node CREATED="1423989864739" ID="ID_1859234538" MODIFIED="1423989872563" TEXT="association">
<node CREATED="1423989943036" ID="ID_1102641212" MODIFIED="1423989950324" TEXT="bind context to transaction"/>
</node>
<node CREATED="1423989873065" ID="ID_198478232" MODIFIED="1423989875380" TEXT="propagation">
<node CREATED="1423989964478" ID="ID_1434750266" MODIFIED="1423990005747" TEXT="sharing context between EMs in a single transaction"/>
</node>
</node>
<node CREATED="1423990017045" ID="ID_32146571" MODIFIED="1423990029064" TEXT="only one context in a transaction"/>
</node>
<node CREATED="1423767983950" HGAP="55" ID="ID_556658583" MODIFIED="1423998709085" POSITION="right" TEXT="EntityManager" VSHIFT="17">
<node CREATED="1423768084698" ID="ID_1292039754" MODIFIED="1423768090434" TEXT="works with @Entities"/>
<node CREATED="1423768072656" ID="ID_1361631833" MODIFIED="1423768076855" TEXT="CRUD operations">
<node CREATED="1423996159949" ID="ID_1808632754" MODIFIED="1423996162449" TEXT="persist()">
<node CREATED="1423996163023" ID="ID_1784781815" MODIFIED="1423996182920" TEXT="takes new object and makes it managed"/>
<node CREATED="1423996183344" ID="ID_1517244654" MODIFIED="1423996199320" TEXT="if oblect is already managed - do nothing"/>
<node CREATED="1423996294394" ID="ID_301716449" MODIFIED="1423996304610" TEXT="if entity is managed, any changes will go to database"/>
<node CREATED="1423996346628" ID="ID_1993452739" MODIFIED="1423996403666" TEXT="outside transaction - TransactionRequiredException (if transaction-scoped EM)"/>
<node CREATED="1423996437488" ID="ID_96227483" MODIFIED="1423996452580" TEXT="if no new entity passed, provider can throw EntityExistsException">
<node CREATED="1423996494220" ID="ID_1986526829" MODIFIED="1423996508081" TEXT="exception can be when context flushed also"/>
</node>
</node>
<node CREATED="1423996218208" ID="ID_665061557" MODIFIED="1423996220645" TEXT="contains()">
<node CREATED="1423996221852" ID="ID_658760822" MODIFIED="1423996233474" TEXT="checks if entity is managed"/>
</node>
<node CREATED="1423996795919" ID="ID_1018733020" MODIFIED="1423996797699" TEXT="find()">
<node CREATED="1423996824674" ID="ID_87635913" MODIFIED="1423996830282" TEXT="finds by primary key"/>
<node CREATED="1423996866921" ID="ID_695469014" MODIFIED="1423996871762" TEXT="returns managed entity"/>
<node CREATED="1423997011589" ID="ID_363549501" MODIFIED="1423997017820" TEXT="if not entity found - returns null"/>
</node>
<node CREATED="1423996914232" ID="ID_1315952056" MODIFIED="1423996917793" TEXT="getReference()">
<node CREATED="1423996918393" ID="ID_1233806399" MODIFIED="1423996925888" TEXT="not loads all entity"/>
<node CREATED="1423996926611" ID="ID_1297606399" MODIFIED="1423996935484" TEXT="we can use it for relationship adding"/>
<node CREATED="1423996970751" ID="ID_1395655136" MODIFIED="1423996977748" TEXT="used for performance optimization"/>
<node CREATED="1423997002289" ID="ID_1523021289" MODIFIED="1423997010518" TEXT="if not entity found - throws exception"/>
<node CREATED="1423997080868" ID="ID_1979202133" MODIFIED="1423997093727" TEXT="if we access not only primary key - throws exception"/>
<node CREATED="1423997155887" ID="ID_1273971239" MODIFIED="1423997170224" TEXT="use carefully! only when we understand that it will give us benefit"/>
</node>
<node CREATED="1423997245846" ID="ID_1374581876" MODIFIED="1423997249463" TEXT="remove()">
<node CREATED="1423997250124" ID="ID_1637658945" MODIFIED="1423997260902" TEXT="actual removing happens when commit transaction"/>
<node CREATED="1423997301214" ID="ID_771910778" MODIFIED="1423997320559" TEXT="attention fo relationships (forein key constraint violation can happen)"/>
<node CREATED="1423997358862" ID="ID_1505788162" MODIFIED="1423997367809" TEXT="you need to clear forein keys first"/>
<node CREATED="1423997374446" ID="ID_1637228822" MODIFIED="1423997382016" TEXT="only managed entity can be removed"/>
<node CREATED="1423997415849" ID="ID_537719834" MODIFIED="1423997422223" TEXT="removed entity can be persisted again"/>
</node>
<node CREATED="1424009461834" ID="ID_617629741" MODIFIED="1424009465284" TEXT="detach()">
<node CREATED="1424009510530" ID="ID_1530978321" MODIFIED="1424009523892" TEXT="changes in entity will not be persisted in database"/>
<node CREATED="1424009689264" ID="ID_1213758980" MODIFIED="1424009700538" TEXT="after detach we can make changes offline"/>
<node CREATED="1424009767488" ID="ID_29411545" MODIFIED="1424009777619" TEXT="problems if we have lazy loading"/>
<node CREATED="1424009818825" ID="ID_1004156901" MODIFIED="1424009826633" TEXT="when entity detatched">
<node CREATED="1424009827375" ID="ID_624217678" MODIFIED="1424009883066" TEXT="tx.commit()"/>
<node CREATED="1424009839216" ID="ID_1398834510" MODIFIED="1424009841711" TEXT="context closed"/>
<node CREATED="1424009848407" ID="ID_196476836" MODIFIED="1424009853351" TEXT="em.clear()"/>
<node CREATED="1424009864266" ID="ID_1723417451" MODIFIED="1424009866348" TEXT="detach()"/>
<node CREATED="1424009873921" ID="ID_930053912" MODIFIED="1424009879800" TEXT="tx.rollback()"/>
</node>
</node>
<node CREATED="1424009594824" ID="ID_1746894605" MODIFIED="1424009597502" TEXT="merge()">
<node CREATED="1424009598112" ID="ID_1451533896" MODIFIED="1424009603687" TEXT="opposite to detach()"/>
<node CREATED="1424009620073" ID="ID_609958901" MODIFIED="1424009660852" TEXT="changes made in detached entity will be owerride current values in persistent context"/>
<node CREATED="1424010412003" ID="ID_1464593814" MODIFIED="1424010416272" TEXT="returns new managed instance"/>
<node CREATED="1424010391811" ID="ID_1237653640" MODIFIED="1424010421567" TEXT="nothing changes in method argument"/>
<node CREATED="1424010525988" ID="ID_1312157914" MODIFIED="1424010542250" TEXT="if entity with ID exists, provider overrides it with argument"/>
<node CREATED="1424010579403" ID="ID_607288769" MODIFIED="1424010588817" TEXT="when invoked for new entity = persist()">
<node CREATED="1424010638432" ID="ID_1204185731" MODIFIED="1424010656044" TEXT="merge returns new managed copy"/>
</node>
</node>
</node>
<node CREATED="1423768175335" ID="ID_1098755705" MODIFIED="1423768192697" TEXT="takes @Entities from Persistence CONTEXT"/>
<node CREATED="1423768258168" ID="ID_1512579359" MODIFIED="1423988595904" TEXT="1. container managed">
<node CREATED="1423987406384" ID="ID_253765057" MODIFIED="1423987521921" TEXT="we use @PersistentContext"/>
<node CREATED="1423987425704" ID="ID_237507338" MODIFIED="1423987452856" TEXT="container manages lifecycle (usually makes proxy)"/>
<node CREATED="1423987461810" ID="ID_710902212" MODIFIED="1423987471144" TEXT="application does not open and close EntityManager"/>
<node CREATED="1423987507891" ID="ID_1512277266" MODIFIED="1423987509215" TEXT="type">
<node CREATED="1423987493946" ID="ID_1333053086" MODIFIED="1423987501264" TEXT="1. Transaction scoped">
<node CREATED="1423987586689" ID="ID_158387257" MODIFIED="1423987714365" TEXT="always one active JTA transaction"/>
<node CREATED="1423987740738" ID="ID_1666490912" MODIFIED="1423987763652" TEXT="new persistence context for each transaction"/>
<node CREATED="1423987784549" ID="ID_1871432291" MODIFIED="1423987802595" TEXT="join to current context and transaction if exists"/>
<node CREATED="1423987827251" ID="ID_1531312442" MODIFIED="1423987835455" TEXT="delete context when transaction ends"/>
</node>
<node CREATED="1423987512496" ID="ID_430057715" MODIFIED="1423987585686" TEXT="2. Extended">
<node CREATED="1423987633030" ID="ID_862413571" MODIFIED="1423987640927" TEXT="many transactions"/>
<node CREATED="1423987641167" ID="ID_104533528" MODIFIED="1423988168961" TEXT="contexts lives same while session bean lives"/>
<node CREATED="1423988194564" ID="ID_878631470" MODIFIED="1423988211999" TEXT="what is a life time of transaction?"/>
<node CREATED="1423988212837" ID="ID_1692712069" MODIFIED="1423988226567" TEXT="are transactions too long? what about blocking?"/>
</node>
<node CREATED="1423988623438" ID="ID_329073563" MODIFIED="1423988628970" TEXT="how is it in Spring?"/>
</node>
<node CREATED="1423989693462" ID="ID_602568304" MODIFIED="1423989759248" TEXT="always use JTA transactions">
<arrowlink DESTINATION="ID_424286497" ENDARROW="Default" ENDINCLINATION="-186;103;" ID="Arrow_ID_1160195418" STARTARROW="None" STARTINCLINATION="-262;-51;"/>
<font NAME="SansSerif" SIZE="13"/>
</node>
</node>
<node CREATED="1423988607574" ID="ID_785400295" MODIFIED="1423988612877" TEXT="2. application managed">
<node CREATED="1423989246714" ID="ID_1072246632" MODIFIED="1423989255583" TEXT="factory.createEntityManager()"/>
<node CREATED="1423989277287" ID="ID_352959869" MODIFIED="1423989289832" TEXT="all managers associated with EntityManagerFactory"/>
<node CREATED="1423989290908" ID="ID_1220634079" MODIFIED="1423989395164" TEXT="when create in container, use @PersistenceUnit for EntityManagerFactory attribute">
<node CREATED="1423989455568" ID="ID_195709666" MODIFIED="1423989465243" TEXT="factory can be created by container"/>
<node CREATED="1423989467418" ID="ID_1088655225" MODIFIED="1423989473701" TEXT="container automatically closes factory"/>
</node>
<node CREATED="1423989422939" ID="ID_1208558523" MODIFIED="1423989430111" TEXT="we need to manyally call close()"/>
<node CREATED="1423989488754" ID="ID_938161629" MODIFIED="1423989506040" TEXT="persistence context lives until EM is closed"/>
<node CREATED="1423989774857" ID="ID_1244265502" MODIFIED="1423989784850" TEXT="can use resource-local and JTA transactions"/>
<node CREATED="1423995987888" ID="ID_1728024456" MODIFIED="1423996009486" TEXT="not propagated automatically to beans (we need to pass it manually as method argument)"/>
</node>
<node CREATED="1423998714981" ID="ID_1706413727" MODIFIED="1423998760805" TEXT="clear()">
<node CREATED="1423998761431" ID="ID_1544718949" MODIFIED="1423998775586" TEXT="cleans pesistence context"/>
<node CREATED="1423998776461" ID="ID_1842775157" MODIFIED="1423998787347" TEXT="can help in unit-tests where we use single EM for all tests"/>
<node CREATED="1423998800094" ID="ID_1472999931" MODIFIED="1423998806270" TEXT="all entities become detached"/>
</node>
<node CREATED="1423998955220" ID="ID_1822757202" MODIFIED="1423998957435" TEXT="flush()">
<node CREATED="1423998958147" ID="ID_59344597" MODIFIED="1423998964644" TEXT="runs SQL queries to DB"/>
<node CREATED="1423998980026" ID="ID_765909702" MODIFIED="1423998993522" TEXT="provider calls it automatically when needed"/>
</node>
</node>
<node CREATED="1423767691670" HGAP="46" ID="ID_437432947" MODIFIED="1423768212750" POSITION="right" TEXT="Persistence Context" VSHIFT="53">
<arrowlink DESTINATION="ID_556658583" ENDARROW="None" ENDINCLINATION="23;18;" ID="Arrow_ID_500322437" STARTARROW="Default" STARTINCLINATION="9;-9;"/>
<arrowlink DESTINATION="ID_1561483896" ENDARROW="Default" ENDINCLINATION="9;-12;" ID="Arrow_ID_502738557" STARTARROW="None" STARTINCLINATION="51;29;"/>
<node CREATED="1423767011817" ID="ID_576862071" MODIFIED="1423767888875" TEXT="managed set of entities in memory"/>
<node CREATED="1423767966155" ID="ID_184309854" MODIFIED="1423767979253" TEXT="entities can be acted by EntityManager"/>
<node CREATED="1423768131240" ID="ID_1322919193" MODIFIED="1423768140464" TEXT="can participate in Transaction">
<node CREATED="1423768141368" ID="ID_1827619435" MODIFIED="1423768154242" TEXT="@Entities will be synchronized with DB"/>
</node>
<node CREATED="1423990042749" FOLDED="true" ID="ID_670301748" MODIFIED="1423990712955" TEXT="type">
<node CREATED="1423990044469" ID="ID_172744922" MODIFIED="1423990049588" TEXT="transaction-scoped">
<node CREATED="1423990058407" ID="ID_1099244442" MODIFIED="1423990071844" TEXT="created for transaction"/>
<node CREATED="1423990072147" ID="ID_1366723782" MODIFIED="1423990079191" TEXT="closed when transaction completes"/>
</node>
<node CREATED="1423990488645" ID="ID_1741389191" MODIFIED="1423990490432" TEXT="extended">
<node CREATED="1423990495347" ID="ID_720168739" MODIFIED="1423990508175" TEXT="same as life of statefull session bean"/>
</node>
</node>
<node CREATED="1423990716445" ID="ID_431270401" MODIFIED="1423990719563" TEXT="collisions">
<node CREATED="1423990903776" ID="ID_478469737" MODIFIED="1423990927750" TEXT="is statefull bean calls stateless"/>
<node CREATED="1423990929009" ID="ID_489825543" MODIFIED="1423990935940" TEXT="both beans have different contexts"/>
<node CREATED="1423990937450" ID="ID_270311354" MODIFIED="1423990946715" TEXT="in this case we can not make common transaction"/>
</node>
</node>
<node CREATED="1423767685773" HGAP="38" ID="ID_1561483896" MODIFIED="1423767825219" POSITION="right" TEXT="Persistence UNIT" VSHIFT="-47">
<arrowlink DESTINATION="ID_1305728914" ENDARROW="Default" ENDINCLINATION="27;-25;" ID="Arrow_ID_722007920" STARTARROW="None" STARTINCLINATION="12;25;"/>
<node CREATED="1423767702346" ID="ID_1974946484" MODIFIED="1423767882306" TEXT="named configuration"/>
<node CREATED="1423767871394" ID="ID_1116711866" MODIFIED="1423767878829" TEXT="List of @Entity classes"/>
<node CREATED="1423767849850" ID="ID_610785177" MODIFIED="1423767851424" TEXT="settings">
<node CREATED="1423767851943" ID="ID_1547988473" MODIFIED="1423767857861" TEXT="JPA provider configuration"/>
<node CREATED="1423767858534" ID="ID_487707712" MODIFIED="1423767862130" TEXT="Database connection"/>
</node>
</node>
<node CREATED="1423766843435" HGAP="34" ID="ID_1305728914" MODIFIED="1423768019799" POSITION="right" TEXT="Configuration" VSHIFT="-15">
<node CREATED="1423766863866" ID="ID_505747174" MODIFIED="1423767933518" TEXT="Java classes with @Entity">
<node CREATED="1423767236708" ID="ID_399417929" MODIFIED="1423767904658" TEXT="POJO with data (attributes, values)"/>
</node>
<node CREATED="1423766871835" ID="ID_1833184416" MODIFIED="1423766876121" TEXT="persistence.xml"/>
</node>
</node>
</map>

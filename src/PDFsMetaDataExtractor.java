import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A class for storing metadata about a SPADE run sourced from the collection of annotated PDFs downloaded from that run
 * on Cytobank.
 * Class has methods for parsing this data out. As of the writing of this comment, the class does all this automatically
 * upon instantiation of the object with a File object pointing to the folder containing the PDFs.
 * 
 * Remember that channel + metric + file does not always have a PDF. There are some default channels without
 * correlate metrics, such as percentotal, count, and percentotalratiolog(fold runs only). The number of these PDFs 
 * will be equivalent to the number of samples. 
 * density occurs only with the non-fold metrics, even in fold runs.
 * @author cciccole
 *
 */
public class PDFsMetaDataExtractor {
   
   protected File _pdfDirectory;
   protected List<String> _listOfPDFs;
   protected boolean _isFoldRun;
   protected List<String> _sampleNames;
   protected int _numberOfSamples;
   protected List<String> _channels;  // note that some channels have no metric (see comment below in method)
   //percenttotalratiolog only appears in fold runs
   private String[] _defaultChannels = {"density", "percentotal", "count", "percentotalratiolog"}; //careful changing this
   //fold and raw_fold only appear in fold runs
   private String[] _defaultMetrics = {"cvs", "raw_medians", "medians", "fold", "raw_fold"};
   
   public PDFsMetaDataExtractor(File pdfDirectory){
      _pdfDirectory = pdfDirectory;
      loadListOfPDFs();
      setIsFoldRun();
      setSampleNames();
      setNumberOfSamples();
      setListOfChannels();
   }

   private void loadListOfPDFs(){
      _listOfPDFs = new ArrayList<String>();
      for (File file : _pdfDirectory.listFiles()) {
         if (file.getName().endsWith("pdf")) {
            _listOfPDFs.add(file.getAbsolutePath());
         }
      }
   }
   
   //uses file names containing "raw_medians" to then get channels. This will miss the channels that are
   //without parameter, such as percenttotal, count, and percenttotalratiolog for fold runs.
   //suggest putting these in with 
   private void setListOfChannels(){
      _channels = new ArrayList<String>();
      ArrayList<String> tempList = new ArrayList<String>();
      //add all the files of a particular metric and file to the temp list
      for (String name : _listOfPDFs){
         if ( name.contains(_defaultMetrics[1]) && name.contains(_sampleNames.get(0) ) )   //arbitrary choice here, but works for fold and non-fold runs
            tempList.add(name);
      }
      
      //now pull out the channel names into channel list using the same arbitrary index and erase the .pdf
      for (String name : tempList){
         _channels.add(name.substring( name.lastIndexOf(_defaultMetrics[1]) + _defaultMetrics[1].length(), (name.length() - 4) ) ) ;
      }
      
      //now add the ones that it won't find because they aren't associated with a metric.
      _channels.add(_defaultChannels[1]);
      _channels.add(_defaultChannels[2]);
      
      //now add the only fold default channel if its a fold run
      if(_isFoldRun)
         _channels.add(_defaultChannels[3]);
   }
   
   private void setNumberOfSamples(){
      _numberOfSamples = _sampleNames.size();
   }
   
   //searches list of PDFs 
   //TODO evaluate whether comparison of number of samples to number of files is better here.
   private void setIsFoldRun(){
      for (int i = 0; i < _listOfPDFs.size(); i++){
         if (_listOfPDFs.get(i).contains("percenttotalratiolog.pdf") ){ //only present in fold runs
            _isFoldRun = true;
            break;
         }
      }
   }
  
   
   private void setSampleNames(){
      _sampleNames = new ArrayList<String>();
      for (int i = 0; i < _listOfPDFs.size(); i++){
         if ( _listOfPDFs.get(i).endsWith("count.pdf") ){
            String fileExtension = _listOfPDFs.get(i);
            int lastSeparator = fileExtension.lastIndexOf( System.getProperty("file.separator") );
            //isolate the sample name by removing filesystem path and _count.pdf
            _sampleNames.add(fileExtension.substring(lastSeparator + 1, fileExtension.length() - 10 ) );
         }
      }
   }
   
   public static void main(String[] args){
      PDFsMetaDataExtractor meta = new PDFsMetaDataExtractor(new File("/Users/cytobankinc/Desktop/pdf-spade-bendall-fold-bubbles"));
      System.out.println(Arrays.toString(meta._listOfPDFs.toArray() ) );
      System.out.println(Arrays.toString(meta._sampleNames.toArray() ) );
      for(String string : meta._channels)
         System.out.println(string);
      System.out.println("total channel count:" + " " + meta._channels.size());
   }
}

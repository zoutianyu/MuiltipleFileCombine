package MuiltiSmallFileCombine;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

import java.io.IOException;

public class MyInputFormat extends CombineFileInputFormat<Text, Text>{
	
	/*
     * ��д����,ֱ�ӷ���false,�������ļ����������и�,��������
     */
    @Override
    protected boolean isSplitable(JobContext context, Path file) {
        return false;
    } 
	/*
	 * ��д�˷���,���ص�CombineFileRecordReaderΪ����ÿ����Ƭ��recordReader,
	 * �ڹ��캯���������Զ����RecordReader����
	 */	
	public RecordReader<Text, Text> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException {
        return new CombineFileRecordReader<Text, Text>((CombineFileSplit) inputSplit, taskAttemptContext, MyRecordReader.class);
    }
}

package MuiltiSmallFileCombine;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

/*
 * �Զ����RecordReader��,��������CombineFileInputSplit���ص�ÿ����Ƭ
 */
public class MyRecordReader extends RecordReader<Text, Text>{

	private CombineFileSplit combineFileSplit; // ��ǰ����ķ�Ƭ
	private Configuration conf; //ϵͳ��Ϣ
	private int curIndex;//��ǰ�����ڼ�����Ƭ
	
	private Text curKey = new Text();//��ǰkey
	private Text curValue = new Text();//��ǰvalue
	private boolean isRead = false;//�Ƿ��Ѿ���ȡ���÷ַ�Ƭ
	private float currentProgress = 0;//��ǰ��ȡ����
	
	private FSDataInputStream fsInputStream;//HDFS�ļ���ȡ��
	
	/*
	 * ���캯���������������,�Զ����InputFormat��ÿ�ζ�ȡ�µķ�Ƭʱ,
	 * ����ʵ�����Զ����RecordReader�������������ж�ȡ
     * @param combineFileSplit   ��ǰ��ȡ�ķ�Ƭ
     * @param taskAttemptContext ϵͳ�����Ļ���
     * @param index              ��ǰ��Ƭ�д�����ļ�����
	 */
	public MyRecordReader(CombineFileSplit combineFileSplit, TaskAttemptContext taskAttemptContext, Integer index)
	{
		this.combineFileSplit = combineFileSplit;
		this.conf = taskAttemptContext.getConfiguration();
		this.curIndex = index;
	}
	
	@Override
	public void close() throws IOException {
		if (fsInputStream != null) {
			fsInputStream.close();
		}
		
	}

	/**
     * ���ص�ǰkey�ķ���
     */
	public Text getCurrentKey() throws IOException, InterruptedException {
		return curKey;
	}

	/**
     * ���ص�ǰvalue�ķ���
     */
	public Text getCurrentValue() throws IOException, InterruptedException {
		return curValue;
	}

	/**
     * ���ص�ǰ�Ĵ������
     */
	public float getProgress() throws IOException, InterruptedException {
		//��õ�ǰ��Ƭ�е����ļ���
		int splitFileNum = combineFileSplit.getPaths().length;
		if (curIndex > 0 && curIndex < splitFileNum) {
			//��ǰ������ļ����������ļ������õ�����Ľ���
			currentProgress = (float)curIndex / splitFileNum;
		}
		return currentProgress;
	}

	/*
	 * ��ʼ��RecordReader��һЩ����(non-Javadoc)
	 */
	@Override
	public void initialize(InputSplit arg0, TaskAttemptContext arg1) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	/*
	 * ����true��ȡ��key��value,֮��indexǰ��,����false�ͽ���ѭ����ʾû���ļ����ݿɶ�ȡ��
	 */
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {

		//û����ȡ�����ļ��Ž��ж�ȡ
		if (!isRead) {
			//ֻʵ���˶�ȡ����Ŀ¼�µ��ļ���û��ʵ�ֶ�ȡ����Ŀ¼����Ŀ¼����ļ�
			//Ĭ�ϵ�TextFileInputFormat����õ�RecoderReader,�ǿ��Զ�ȡ��Ŀ¼����ļ���
			
			//���ݵ�ǰ���ļ������ӵ�ǰ��Ƭ���ҵ���Ӧ���ļ�·��
			Path path = combineFileSplit.getPath(curIndex);
			
			//��ȡ��Ŀ¼����ΪKeyֵ
			curKey.set(path.getParent().getName());
			//�ӵ�ǰ��Ƭ�л�õ�ǰ�ļ��ĳ���
			byte[] content = new byte[(int) combineFileSplit.getLength(curIndex)];
			try {
				//��ȡ���ļ�����
				FileSystem fs = path.getFileSystem(conf);
				FileStatus st = fs.getFileStatus(path);
				if (!st.isFile()) {
					return false;
				}
				fsInputStream = fs.open(path);
				fsInputStream.readFully(content);
			} catch (Exception ignored) {
            }finally {
				if (fsInputStream != null) {
					fsInputStream.close();
				}
			}
			//�����ļ�������Ϊvalueֵ
			curValue.set(content);
			isRead = true;
			return true;
		}
		return false;
	}

}

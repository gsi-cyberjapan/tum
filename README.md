# tile upload manager�̎d�l

�^�C���A�b�v���[�h�}�l�[�W���[�̎d�l�͎��̂Ƃ���ł��B    

# tile upload manager�̊T�v

�n���@�n�}�^�C���A�b�v���[�_�̎Q�Ǝ����ł��B  
�_�E�����[�h�ς݂̒n���@�n�}�^�C����AmazonDynamoDB��p����AmazonS3�փA�b�v���[�h���܂��B  
Amazon Web Service��p���邽�߁A���g�p���ɂ�IAM�F�؏��i�A�N�Z�X�L�[�A�V�[�N���b�g�L�[�j���K�v�ł��B�@

# tile upload manager�̍\��

��tum  
�E�\�[�X�R�[�h�ꎮ  
�EParams.xml  
�EAwsCredentials.properties  


��AWS  
�ES3  
 Amazon Web Service�̃I�u�W�F�N�g�X�g���[�W�B�^�C���̕ۑ���Ƃ��Ďg�p���܂��B  

�EDynamoDB  
 Amazon Web Service�̃L�[�o�����[�X�g�A�B�^�C�����̕ۑ���Ƃ��Ďg�p���܂��B  
 �ۑ�������́A�^�C���̃t���p�X(xyz/{t}/{z}/{x}/{y}.{ext})�AMD5SUM�A�^�C���̍��W({z}/{x}/{y})�A�^�C���̎��({t})�A�g���q�i{ext}�j�ł��B  
�^�C���A�b�v���[�h����DynamoDB�ɂă^�C���̃t���p�X��MD5SUM�ɂ��˂����킹���s���A���ɓo�^�ς݂ł���΃A�b�v���[�h���Ȃ��d�g�݂ƂȂ�܂��B  

## Params.xml

Params.xml�͈ȉ��̂悤�ɍ쐬���Ă��������B

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Params>
  <Proxy>
    <Host>input host</Host>
    <Port>input port</Port>
  </Proxy>
</Params>
```

input host�Ainput port�ɂ͂��ꂼ��v���L�V��host���A�|�[�g�ԍ����L�ڂ��Ă��������B�@�@

## AwsCredentials.properties

AwsCredentials.properties�͈ȉ��̂悤�ɍ쐬���Ă��������B

```
accessKey = xxx
secretKey = yyy
```

xxx�Ayyy�͂��ꂼ��IAM�F�؏��ɒu�������Ă��������B

# tum�g�p����
�{�y�[�W�ł��Љ����@�͈��ł��B  
�Ȃ��{�y�[�W�ł��Љ����@��Windows�ł̎g�p��z�肵�Ă��܂��B  


**1.AWS�̏���**  
1-0.AWS�̃A�J�E���g���������łȂ����́A���LURL���A�J�E���g���쐬���Ă��������B  
https://aws.amazon.com/jp/register-flow/  
1-1.Admin������IAM���[�U�[���쐬���A�A�N�Z�X�L�[�ƃV�[�N���b�g�L�[���擾���Ă��������B  
1-2.S3�̃o�P�b�g��1�쐬���Ă��������B  
1-3.DynamoDB�̃e�[�u����1�쐬���Ă��������B  
�܂��A�e�[�u���̃p�[�e�B�V�����L�[�A�\�[�g�L�[�͂��ꂼ�ꉺ�L�̂悤�ɐݒ肵�Ă��������B  
�E�p�[�e�B�V�����L�[: tileInfo (������)  
�E�\�[�g�L�[: md5sum (������)  


**2.tum�̏���**  
�\�[�X�R�[�h��AmazonS3�̃o�P�b�g����AmazonDynamoDB�̃e�[�u���������L�ڂ���K�v�����邽�߁A�J������p�ӂ��܂��B  

2-0.Eclipse����  
�EEclipse���_�E�����[�h�A�C���X�g�[�����Ă��������B�����Java��I�����Ă��������B���LURL�Q��  
http://mergedoc.osdn.jp/  
�EAWS Toolkit for Eclipse��Eclipse�ɃC���X�g�[�����Ă��������B���LURL�Q��  
https://aws.amazon.com/jp/eclipse/  

2-1.Eclipse��AWS Toolkit for AWS�̃v���_�E�����j���[���A�uNew AWS JAVA Project�v��I�����܂��B  
2-2.�쐬���ꂽ�v���W�F�N�g���́usrc�v���E�N���b�N���A�\�����ꂽ�R���e�L�X�g���j���[����u�V�K�v�u�p�b�P�[�W�v��I�����܂��B  
2-3.�\�����ꂽ�V�K�p�b�P�[�W�쐬��ʂɂāu���O�v�̗���tum�Ɠ��͂��A�����{�^�����������܂��B  
2-4.�쐬���ꂽtum�p�b�P�[�W���E�N���b�N���A�\�����ꂽ�R���e�L�X�g���j���[����u�G�N�X�v���[���[�ŊJ���v��I�����܂��B  
2-5.�\�����ꂽ�t�H���_�Ƀ\�[�X�R�[�h�ꎮ���i�[���AEclipse���ċN�����܂��B  
2-6.�ǂݍ��܂ꂽ�\�[�X�R�[�h���m�F���A"input your bucket name"��input your table name"�Ƃ���ӏ����u1.AWS�̏����v�ō쐬�������O�ɕύX���܂��B

# �^�C���A�b�v���[�h�̕��@

**1.Eclipse������s������@**  
1-1.Eclipse���N�����܂��B�utum�g�p�����v2-1�ō쐬�����v���W�F�N�g���E�N���b�N���A�\�����ꂽ�R���e�L�X�g���j���[����u�G�N�X�v���[���[�ŊJ���v��I�����܂��B  
1-2.�\�����ꂽ�t�H���_��Params.xml���i�[���܂��Bbin�f�B���N�g������AwsCredentials.properties���i�[���܂��B  
1-3.�utum�g�p�����v2-1�ō쐬�����v���W�F�N�g���E�N���b�N���A�\�����ꂽ�R���e�L�X�g���j���[����u���s�v �u���s�̍\���v��I�����܂��B  
1-4.�\�����ꂽ���s�\�����j���[����A�uJava�A�v���P�[�V�����v���E�N���b�N���A�u�V�K�v��I�����܂��B  
1-5.�\�����ꂽ�V�K�\���́u���C���v�^�u�ŁA�u�Q�Ɓv�{�^�����������A�utum�g�p�����v2-1�ō쐬�����v���W�F�N�g��I�����܂��B  
1-6.�u�����v�^�u���́u�v���O�����̈����v�ɉ��L���Q�l��[������] [������]���L�����܂��B  

��: C:\Users\Administrator\up\xyz\�t�H���_�ɃA�b�v���[�h����^�C�����i�[����Ă���ꍇ  
tum C:\Users\Administrator\up\xyz

**2.�R�}���h�v�����v�g������s������@**  
2-1.Eclipse���N�����܂��B�utum�g�p�����v2-1�ō쐬�����v���W�F�N�g���E�N���b�N���A�\�����ꂽ�R���e�L�X�g���j���[����u�G�N�X�|�[�g�v��I�����܂��B  
2-2.�u���s�\jar�t�@�C���v��I�����A�C�ӂ̃G�N�X�|�[�g���ݒ肵�܂��B  
2-3.�u���C�u�����[�����v�́u���������JAR�ɕK�{���C�u�����[���p�b�P�[�W�v��I�����܂��B  
2-4.�u�����v�{�^�����������܂��B  
2-5.2-2�Őݒ肵���G�N�X�|�[�g��ɁAParams.xml��AwsCredentials.properties���i�[���܂��B  
2-6.�R�}���h�v�����v�g�ŃG�N�X�|�[�g��t�H���_�Ɉړ����A���L�R�}���h�����s���܂��B  
java -jar tum [������] [������]  
�������ɂ�tum�A�������ɂ̓A�b�v���[�h����^�C�������݂���t�H���_�܂ł̃p�X���L�����Ă��������B  
��: C:\Users\Administrator\up\xyz\�t�H���_�ɃA�b�v���[�h����^�C�����i�[����Ă���ꍇ  
java -jar tum.jar tum C:\Users\Administrator\up\xyz�@�@

# �ژ^�y�уR�R�^�C���A�b�v���[�h�̎d�g��
�ژ^�y�уR�R�^�C���̏ڂ����d�l�͉��LURL�����m�F���������B  
https://github.com/gsi-cyberjapan/mokuroku-spec  
https://github.com/gsi-cyberjapan/cocotile-spec  

**1.�ژ^�A�b�v���[�h�̎d�g��**  
�ژ^�ɏ������܂��f�[�^�̓p�X,�ŏI�X�V����,�T�C�Y,MD5SUM�ł��B  
�����̃f�[�^���^�C���했��1�^�C������S3���擾���A1�s�������o������mokuroku.csv���쐬���܂��B  
mokuroku.csv�쐬���gzip�ɂĈ��k���AS3��{t}�i�^�C����j�t�H���_�̒����ɃA�b�v���[�h����܂��B  

���s�R�}���h  
java -jar  [������] [������]  
�������ɂ�mokuroku�A�������ɂ͈ꎞ�g�p�t�H���_�̃p�X���L�����Ă��������B  
�������Ŏw�肳�ꂽ�t�H���_���g�p����mokuroku���쐬/�A�b�v���[�h���܂��B  
��: C:\temp�t�H���_���ꎞ�g�p�t�H���_�Ƃ���ꍇ  
java -jar tum.jar mokuroku C:\temp�@�@
�q�[�v�������̍ő�l��512m�ȏ㐄��  

**2.�R�R�^�C���A�b�v���[�h�̎d�g��**  
�R�R�^�C���ɏ������܂��f�[�^�͂��̍��W�ɑ��݂���^�C���̎�ނł��B  
�^�C���̎�ޖ��ɖژ^���Q�Ƃ��A���W���ɃR�R�^�C�����쐬�A�����o�����s���܂��B  
�R�R�^�C���쐬��́A�o�P�b�g������cocotile�t�H���_�ɍ��W���̃t�H���_���쐬���A���̃t�H���_�ɃA�b�v���[�h���܂��B  

���s�R�}���h  
java -jar  [������] [������]  
�������ɂ�cocotile�A�������ɂ͈ꎞ�g�p�t�H���_�̃p�X���L�����Ă��������B  
�������Ŏw�肳�ꂽ�t�H���_���g�p����cocotile���쐬/�A�b�v���[�h����܂��B�R�R�^�C���쐬�p�ꎞ�t�@�C�������t�H���_�ɍ쐬����܂��B    
��: C:\cocotemp�t�H���_���ꎞ�g�p�t�H���_�Ƃ���ꍇ  
java -jar tum.jar mokuroku C:\cocotemp�@�@
�q�[�v�������̍ő�l��512m�ȏ㐄��  

# ������ɂ���

���L���ɂē���m�F�ς݂ł��B
�Ȃ��A�S�Ă̊��ɂ����铮���ۏႷ����̂ł͂���܂���B

- Windows7 32bit
- ������ 4GB
- �n�[�h�f�B�X�N�i�g�p�ʁj 40MB
- CD-ROM�h���C�u�@�s�v
- �C���^�[�l�b�g�� - �펞�ڑ��ł���u���[�h�o���h���i���o�C���[�����͏����j�ł����p���������B
- Java �o�[�W����1.8.0  �q�[�v�������T�C�Y��512m�ɂĎ��s

# ���g�p���̂�����  
**tum���s��**  
- �������Ŏw�肷��xyz�܂ł̃p�X�ɁA�uxyz�v�Ƃ�����������܂܂Ȃ��悤���肵�܂��B  
��j  
��C:\test\123\xyz  
��C:\test\xyz123\xyz  

- xyz�t�H���_�Ɠ����ʒu�Ɂuxyz�v�Ƃ�����������܂ރt�H���_��u���Ȃ��悤���肢���܂��B  
��j  
��C:\test\123�t�H���_�Ɂuxyz�v�t�H���_�̂�  
��C:\test\123�t�H���_�Ɂuxyz�v�t�H���_�Ɓuold_xyz�v�t�H���_������  

**cocotile���s��**�@�@
- �������Ŏw�肷��h���C�u�̋󂫗e�ʂ�500GB�ȏ�ł��鎖�����m�F���������B�K�v�e�ʂ͑�������\��������܂��B  

- �������cocotile���d�Ɏ��s���Ȃ��悤���肢���܂��B  

- cocotile���s�O�ɁA�������Ŏw�肷��t�H���_���Ɂucocotile�v�t�H���_�����݂��Ȃ������m�F���A���݂���ꍇ�͍폜����܂��悤���肢���܂��B  

- �ꎞ�t�H���_�܂ł̃p�X�Ɂucocotile�v�Ƃ�����������܂܂Ȃ��悤���肢���܂��B  
��j  
��C:\cocotemp\  
��C:\cocotile\cocotemp\  
 
- �ꎞ�t�H���_���ɂ̓v���O�������g�p����ꎞ�t�@�C�����쐬����܂��B�v���O�����I���܂ŁA�������폜����Ȃ��悤���肢���܂��B  

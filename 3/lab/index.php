<?php
class contact
{
    public $email;
    public $name;
    public $phone;
    public $address;
}

class Example
{
	private $hook;
	function __construct(){
	// some PHP code...
	}
	function __wakeup(){
		 if(isset($this->hook)){
            $flag = file_get_contents('Flag.txt');
			echo "<h3>".$flag."</h3>";
		 }
	}
}
function unserialize_object($serializedstring) {
    try{
        $variables = array();
        $a = preg_split("/(\w+)\|/", $serializedstring, -1, PREG_SPLIT_NO_EMPTY | PREG_SPLIT_DELIM_CAPTURE);
        $counta = count($a);
        for ($i = 0; $i < $counta; $i = $i + 2) {
            $variables[$a[$i]] = unserialize($a[$i + 1]);
        }
        return $variables;
    }
    catch(Exception $e) {
        echo '<div class="alert alert-success alert-dismissible show">'.$variables.'</div>';
    }

}

function contact($serialize_object) {
    $serialize_object = urldecode($serialize_object);
    unserialize_object($serialize_object);
}
function validate_email($email) {
    if (filter_var($email, FILTER_VALIDATE_EMAIL)) {
        return 1;
    }

    return -1;
}

if(isset($_POST['submit'])) {
    $object = new contact();
    $object->email =  $_POST['contact_email'];
    $object->name  =  $_POST['contact_name'];
    $object->phone  =  $_POST['contact_phone'];
    $object->address  =  urlencode($_POST['contact_address']);
    $serialize_object = "OBJECTION|".serialize($object);
    $condition = preg_match('/%22|%7C|%23|%26|%5E|%27|%29|%28/', $object->address );
    if (validate_email($object->email) === -1) {
        $error =  $object->email . _(" không hợp lệ!");
    }elseif ($condition){
        $suggest = $serialize_object;
        contact($serialize_object);
    } 
    else {
        $success =  "Chúng tôi sẽ liên hệ với bạn sau";
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Q1ZFLTIwMjEtMzYzOTQ=</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="col-md-12">
    <div class="panel panel-default">
        <div class="panel-heading">
            <?php echo _("Nhập thông tin liên hệ"); ?>
        </div>

        <div class="panel-body">
            <form method="post" class="form-horizontal" enctype="multipart/form-data">

                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php echo _("Email:"); ?></label>
                    <div class="col-sm-6">
                        <input type="text" name="contact_email" class="form-control">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php echo _("Name:"); ?></label>
                    <div class="col-sm-6">
                        <input type="text" name="contact_name" class="form-control">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php echo _("SĐT:"); ?></label>
                    <div class="col-sm-6">
                        <input type="text" name="contact_phone" class="form-control">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php echo _("Địa chỉ:"); ?></label>
                    <div class="col-sm-6">
                        <input type="text" name="contact_address" class="form-control">
                    </div>
                </div>


                <div class="form-group" style="text-align:center;">
                    <div class="col-sm-3">
                    </div>
                    <div class="col-sm-6">
                        <button style="width:100px" class="btn btn-primary form-control" name="submit" type="submit"><?php echo _("Gửi"); ?></button>
                    </div>
                </div>

                <?php if (isset($error)) { ?>
                <div class="alert alert-danger alert-dismissible show">
                    <?php echo $error;?>
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                </div>
                <?php }?>
                <?php if (isset($suggest)) { ?>
                <div class="alert alert-info alert-dismissible show">
                    <?php echo $suggest;?>
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                </div>
                <?php }?>
                <?php if (isset($success)) { ?>
                <div class="alert alert-success alert-dismissible show">
                    <?php echo $success;?>
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                </div>
                <?php }?>
            </form>
        </div>
    </div>

</div>
<!-- index.php.bak -->
</div>
</body>
</html>



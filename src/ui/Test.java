package ui;

import javax.xml.crypto.Data;
import java.util.function.Function;

interface DataProcessor {
    String encrypt(String data);
    int getSize(String data);
}


class ConcreteProcessor implements DataProcessor {
    private DataProcessor controller; // Tham chiếu đến controller

    // Phương thức để gán Controller
    public void setController(DataProcessor controller) {
        this.controller = controller;
    }

    // Phương thức tổng quát để lấy giá trị
    private <T> T resolveValue(Function<DataProcessor, T> function) {
        // Kiểm tra controller và lấy giá trị theo function truyền vào
        return function.apply(controller != null ? controller : this);
    }

    @Override
    public String encrypt(String data) {
        System.out.println("ConcreteProcessor: Encrypting data...");
        int size = resolveValue(dp -> dp.getSize(data)); // Lấy size từ controller hoặc ConcreteProcessor
        System.out.println("ConcreteProcessor: Size used for encryption: " + size);
        return "Encrypted(" + data + ", size=" + size + ")";
    }

    public String decrypt(String data) {
        System.out.println("ConcreteProcessor: Decrypting data...");
        int size = resolveValue(dp -> dp.getSize(data)); // Lấy size từ controller hoặc ConcreteProcessor
        System.out.println("ConcreteProcessor: Size used for decryption: " + size);
        return "Decrypted(" + data + ", size=" + size + ")";
    }

    @Override
    public int getSize(String data) {
        System.out.println("ConcreteProcessor: Calculating size...");
        return data.length(); // Logic mặc định
    }

    public int getOtherValue(String data) {
        System.out.println("ConcreteProcessor: Calculating other value...");
        return data.length() * 2; // Một ví dụ khác để trả về giá trị khác
    }
}


abstract class ControllerDecorator implements DataProcessor {
    protected final ConcreteProcessor concrete; // Tham chiếu đến Concrete

    public ControllerDecorator(ConcreteProcessor concrete) {
        this.concrete = concrete;
    }

    @Override
    public String encrypt(String data) {
        return concrete.encrypt(data);
    }

    @Override
    public int getSize(String data) {
        // Mặc định gọi getSize của Concrete
        return concrete.getSize(data);
    }
}

class CustomController extends ControllerDecorator {
    public CustomController(ConcreteProcessor concrete) {
        super(concrete);
    }

    @Override
    public int getSize(String data) {
        // Lấy kết quả gốc từ Concrete
        int originalSize = super.getSize(data);
        System.out.println("CustomController: Original size from Concrete: " + originalSize);

        // Thực hiện logic tùy chỉnh
        int modifiedSize = originalSize * 2;
        System.out.println("CustomController: Modified size: " + modifiedSize);

        return modifiedSize;
    }
}

public class Test {
    public static void main(String[] args) {
        // Tạo ConcreteProcessor
        ConcreteProcessor concrete = new ConcreteProcessor();

        // Tạo ControllerDecorator
        CustomController customController = new CustomController(concrete);
        concrete.setController(customController);
        // Gọi encrypt (sẽ gọi getSize của CustomController)
        String data = "HelloWorld";
        String encryptedData = customController.encrypt(data); // Sử dụng CustomController
        System.out.println("Final Encrypted Data: " + encryptedData);
    }
}

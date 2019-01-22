//Adam Granieri
//COE 1501 Algorithm Implementation
//Fall 2018

import java.util.Random;
import java.math.BigInteger;

public class LargeInteger {
	
	private final byte[] TWO = {(byte) 2};
	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		LargeInteger sum, multiplicant = this, shift  = other;
		if(this.isNegative()){
			multiplicant = this.negate();
		}
		if(other.isNegative()){
			shift = other.negate();
		}
		sum = new LargeInteger(new byte[multiplicant.length() + shift.length()]);
		for(int i = multiplicant.length()-1; i >= 0; i--){
			int checkVal = 1;
			for(int j = 8; j > 0; j--){
				if((multiplicant.getVal()[i] & checkVal) == checkVal){
					sum = sum.add(shift);
				}
				shift = shift.shiftLogicalLeft();
				checkVal = checkVal << 1;
			}
		}
		if(this.isNegative() == other.isNegative()){
			if(sum.isNegative()){
				return reduce(sum.negate());
			}else{
				return reduce(sum);
			}
		}else{
			if(sum.isNegative()){
				return reduce(sum);
			}else{
				return reduce(sum.negate());
			}
		}
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	public LargeInteger[] XGCD(LargeInteger other) {
		if(other.equals(new LargeInteger(ZERO))){
			return new LargeInteger[]{this, new LargeInteger(ONE), new LargeInteger(ZERO)};
		}else{
			LargeInteger[] result = other.XGCD(this.mod(other));
			return new LargeInteger[]{result[0], reduce(result[2]), reduce(result[1].subtract(this.divide(other).multiply(result[2])))};
		}
	 }

	/**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n mod value to use
	  * @return this^y mod n
	  */
	public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
		if(y.equals(new LargeInteger(ONE).negate())){
			LargeInteger[] GCD = n.XGCD(this);
			if(GCD[2].isNegative()){
				return n.add(GCD[2]);
			}else {
				return GCD[2];
			}
		}else{
			boolean negative = false;
			if(y.isNegative()){
				negative = true;
				y = y.negate();
			}
			LargeInteger result = new LargeInteger(ONE);
			LargeInteger value = this.mod(n);
			LargeInteger pow = y;
			while (!pow.isNegative() && !pow.equals(new LargeInteger(ZERO))) {
				if (pow.mod(new LargeInteger(TWO)).equals(new LargeInteger(ONE))) {
					result = (result.multiply(value)).mod(n);
				}
				pow = pow.shiftLogicalRight();
				value = (value.multiply(value)).mod(n);
			}
			if(negative){
				return result.modularExp(new LargeInteger(ONE).negate(), n);
			}
			return result;
		}
	 }

	//helper methods
	public LargeInteger shiftLogicalLeft(){
		byte[] result;
		if((val[0] & 0xC0) == 0x40){
			result = new byte[val.length + 1];
			result[0] = (byte)0x00;
		}else if((val[0] & 0xC0) == 0x80) {
			result = new byte[val.length + 1];
			result[0] = (byte)0xFF;
		}else{
			result = new byte[val.length];
		}
		int oldShiftVal = 0, shiftVal;
		for(int i = 1; i <= val.length; i++){
			shiftVal = val[val.length - i] >> 7 & 0x01;
			result[result.length - i] = (byte)((val[val.length - i] << 1) & 0xfe);
			result[result.length - i] |= oldShiftVal;
			oldShiftVal = shiftVal;
		}
		return new LargeInteger(result);
	}

	public LargeInteger shiftLogicalRight(){
		byte[] result;
		int i;
		if(val[0] == 0x00 && (val[1] & 0x80) == 0x80){
			result = new byte[val.length - 1];
			i = 1;
		}else{
			result = new byte[val.length];
			i = 0;
		}
		int oldShiftVal, shiftVal;
		if(this.isNegative()){
			oldShiftVal = 1;
		}else{
			oldShiftVal = 0;
		}
		for(int j = 0; j < result.length; j++, i++){
			shiftVal = val[i] & 0x01;
			result[j] = (byte)((val[i] >> 1) & 0x7f);
			result[j] |= oldShiftVal << 7;
			oldShiftVal = shiftVal;
		}
		return reduce(new LargeInteger(result));
	}

	public LargeInteger mod(LargeInteger other){
		LargeInteger quotient = this.divide(other);
		return reduce(this.subtract(other.multiply(quotient)));
	}

	public LargeInteger divide(LargeInteger other){
		LargeInteger quotient = new LargeInteger(ZERO), accumulator = this, denominator = other;
		if(this.isNegative()){
			accumulator = this.negate();
		}
		if(other.isNegative()){
			denominator = other.negate();
		}
		int shiftCount = 0;
		while(!accumulator.subtract(denominator).isNegative()){
			denominator = denominator.shiftLogicalLeft();
			shiftCount++;
		}
		denominator = denominator.shiftLogicalRight();
		for(; shiftCount > 0; shiftCount--){
			quotient = quotient.shiftLogicalLeft();
			if(!accumulator.subtract(denominator).isNegative()){
				accumulator = accumulator.subtract(denominator);
				quotient = quotient.add(new LargeInteger(ONE));
			}
			denominator = denominator.shiftLogicalRight();
		}
		if(this.isNegative() == other.isNegative()){
			if(quotient.isNegative()){
				return reduce(quotient.negate());
			}else{
				return reduce(quotient);
			}
		}else{
			if(quotient.isNegative()){
				return reduce(quotient);
			}else{
				return reduce(quotient.negate());
			}
		}
	}

	public LargeInteger reduce(LargeInteger other){
		if(other.length() > 1 && (((other.getVal()[0] & 0xff) == 0x00 && ((other.getVal()[1] & 0x80) == 0x00)) || ((other.getVal()[0] & 0xff) == 0xff && (other.getVal()[1] & 0x80) == 0x80))){
			byte[] shrunk = new byte[other.length() - 1];
			System.arraycopy(other.getVal(), 1, shrunk, 0, shrunk.length);
			return reduce(new LargeInteger(shrunk));
		}
		return other;
	}

	public boolean equals(LargeInteger other){
		if(this.length() != other.length()){
			return false;
		}
		for(int i = 0; i < this.length(); i++){
			if(this.getVal()[i] != other.getVal()[i]){
				return false;
			}
		}
		return true;
	}

	public LargeInteger store(){
		byte[] store = val;
		while(store.length > 64){
			byte[] result = new byte[this.length() - 1];
			System.arraycopy(store, 1, result, 0, result.length);
			store = result;
		}
		while(this.length() < 64) {
			byte[] result = new byte[this.length() + 1];
			if ((store[0] & 0x80) == 0x80){
				result[0] = (byte) 0xFF;
			}else{
				result[0] = (byte)0x00;
			}
			System.arraycopy(store, 0, result, 1, result.length-1);
			store = result;
		}
		return new LargeInteger(store);
	}

}
